package dev.sviri.volley;

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

    private WebSocketSession initiatorSession = null;
    private WebSocketSession followerSession = null;
    private final Object sessionsLock = new Object();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        synchronized (sessionsLock) {
            var signal = objectMapper.readValue(message.getPayload(), SignalMessage.class);
            switch (signal.type()) {
                case INITIATE -> {
                    var uidAndRoomId = signal.sdp().split(",");
                    Room room = roomService.findRoom(UUID.fromString(uidAndRoomId[1]));
                    if (room.initiator().uid().toString().equals(uidAndRoomId[0])) {
                        initiatorSession = session;
                    } else {
                        followerSession = session;
                    }
                    beginRTCNegotiationIfNeeded();
                }
                case OFFER -> followerSession.sendMessage(message);
                case ANSWER -> initiatorSession.sendMessage(message);
                case ICE_CANDIDATE -> {
                    if (session.equals(initiatorSession)) {
                        followerSession.sendMessage(message);
                    }
                }
            }
        }
    }


    private void beginRTCNegotiationIfNeeded() throws IOException {
        if (initiatorSession != null && followerSession != null) {
            initiatorSession.sendMessage(new TextMessage(BEGIN_SYNC_MESSAGE));
        }
    }
}
