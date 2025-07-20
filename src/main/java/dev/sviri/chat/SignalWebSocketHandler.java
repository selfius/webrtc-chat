package dev.sviri.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
class SignalWebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(SignalWebSocketHandler.class);

    private final ObjectMapper objectMapper;
    private final String BEGIN_SYNC_MESSAGE;
    private final RoomService roomService;

    SignalWebSocketHandler(ObjectMapper objectMapper, RoomService roomService) {
        this.objectMapper = objectMapper;
        this.roomService = roomService;
        try {
            BEGIN_SYNC_MESSAGE = objectMapper.writeValueAsString(new SignalMessage(SignalMessageType.START_SYNC, null));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    record SessionPair(WebSocketSession initiator, WebSocketSession follower) {
        SessionPair withInitiator(WebSocketSession initiator) {
            return new SessionPair(initiator, this.follower);
        }

        SessionPair withFollower(WebSocketSession follower) {
            return new SessionPair(this.initiator, follower);
        }
    }

    Map<UUID, SessionPair> sessionsByRoom = new ConcurrentHashMap<>();
    Map<String, UUID> roomsUidsBySessionIds = new ConcurrentHashMap<>();

    private WebSocketSession getFollower(WebSocketSession session) {
        SessionPair sessionPair = sessionsByRoom.get(roomsUidsBySessionIds.get(session.getId()));
        return sessionPair.follower();
    }

    private WebSocketSession getInitiator(WebSocketSession session) {
        SessionPair sessionPair = sessionsByRoom.get(roomsUidsBySessionIds.get(session.getId()));
        return sessionPair.initiator();
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        var signal = objectMapper.readValue(message.getPayload(), SignalMessage.class);
        switch (signal.type()) {
            case INITIATE -> {
                var uidAndRoomId = signal.sdp().split(",");
                Room room = roomService.findRoom(UUID.fromString(uidAndRoomId[1]));
                if (room.initiator().uid().toString().equals(uidAndRoomId[0])) {
                    sessionsByRoom.compute(room.uuid(),
                            (unused, value) -> value == null ?
                                    new SessionPair(session, null) : value.withInitiator(session)
                    );
                } else {
                    sessionsByRoom.computeIfPresent(room.uuid(),
                            (unused, value) -> value.withFollower(session));
                    if (!sessionsByRoom.containsKey(room.uuid())) {
                        throw new IllegalStateException("Couldn't insert follower into an empty room");
                    }
                }
                roomsUidsBySessionIds.put(session.getId(), room.uuid());
                beginRTCNegotiationIfNeeded(session);
            }
            case OFFER -> getFollower(session).sendMessage(message);
            case ANSWER -> getInitiator(session).sendMessage(message);
            case ICE_CANDIDATE -> {
                if (session.equals(getInitiator(session))) {
                    getFollower(session).sendMessage(message);
                }
            }
        }
    }


    private void beginRTCNegotiationIfNeeded(WebSocketSession session) throws IOException {
        WebSocketSession initiator = getInitiator(session);
        if (initiator != null && getFollower(session) != null) {
            initiator.sendMessage(new TextMessage(BEGIN_SYNC_MESSAGE));
        }
    }
}
