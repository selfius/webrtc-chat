package dev.sviri.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
class SignalWebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(SignalWebSocketHandler.class);

    private final ObjectMapper objectMapper;
    private final RoomService roomService;
    private final UserService userService;
    private final TURNPasswordGeneratorService turnPasswordGeneratorService;

    SignalWebSocketHandler(ObjectMapper objectMapper, RoomService roomService, UserService userService,
                           TURNPasswordGeneratorService turnPasswordGeneratorService) {
        this.objectMapper = objectMapper;
        this.roomService = roomService;
        this.userService = userService;
        this.turnPasswordGeneratorService = turnPasswordGeneratorService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        var signal = objectMapper.readValue(message.getPayload(), SignalMessage.class);
        Room room = roomService.findRoom(signal.roomId());
        User user = new User(signal.senderUserId());
        switch (signal.type()) {
            case INITIATE -> {
                if (!room.initiator().uid().equals(signal.senderUserId())) {
                    room.setFollower(user);
                    roomService.updateRoom(room);
                }
                userService.bindUser(user, session);
                beginRTCNegotiationIfNeeded(room);
            }
            case OFFER -> userService.messageUser(room.follower(), message.asBytes());
            case ANSWER -> userService.messageUser(room.initiator(), message.asBytes());
            case ICE_CANDIDATE -> {
                if (user.uid().equals(room.initiator().uid())) {
                    userService.messageUser(room.follower(), message.asBytes());
                } else {
                    userService.messageUser(room.initiator(), message.asBytes());
                }
            }
        }
    }

    private void beginRTCNegotiationIfNeeded(Room room) throws IOException {
        if (room.follower() == null) {
            return;
        }
        var beginSyncMessage = objectMapper.writeValueAsBytes(new SignalMessage(SignalMessageType.START_SYNC,
                turnPasswordGeneratorService.generatePassword()
        ));
        userService.messageUser(room.initiator(), beginSyncMessage);

        var stunCred = objectMapper.writeValueAsBytes(new SignalMessage(SignalMessageType.STUN_CREDENTIALS,
                turnPasswordGeneratorService.generatePassword()
        ));
        userService.messageUser(room.follower(), stunCred);
    }
}
