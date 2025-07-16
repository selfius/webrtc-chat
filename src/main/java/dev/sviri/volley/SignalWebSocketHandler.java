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

@Component
class SignalWebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(SignalWebSocketHandler.class);

    private final ObjectMapper objectMapper;
    private final String BEGIN_SYNC_MESSAGE;

    SignalWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        try {
            BEGIN_SYNC_MESSAGE = objectMapper.writeValueAsString(new SignalMessage(SignalMessageType.START_SYNC, null));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    //todo this is not thread safe at all lol
    private WebSocketSession initiatorSession = null;
    private WebSocketSession followerSession = null;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        var signal = objectMapper.readValue(message.getPayload(), SignalMessage.class);
        switch (signal.type()) {
            case INITIATOR -> {
                initiatorSession = session;
                beginRTCNegotiationIfNeeded();
            }
            case FOLLOWER -> {
                followerSession = session;
                beginRTCNegotiationIfNeeded();
            }
            default -> log.atWarn().log("Don't know what to do with message of {} type", signal.type());
        }
    }


    private void beginRTCNegotiationIfNeeded() throws IOException {
        if (initiatorSession != null && followerSession != null) {
            initiatorSession.sendMessage(new TextMessage(BEGIN_SYNC_MESSAGE));
            followerSession.sendMessage(new TextMessage(BEGIN_SYNC_MESSAGE));
        }
    }
}
