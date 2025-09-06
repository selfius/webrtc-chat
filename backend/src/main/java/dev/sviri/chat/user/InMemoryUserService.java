package dev.sviri.chat.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Profile("default")
public class InMemoryUserService implements UserService {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserService.class);

    private final Map<UUID, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    public void bindUser(User user, WebSocketSession webSocketSession) {
        userSessions.put(user.uid(), webSocketSession);
    }

    @Override
    public void messageUser(User user, byte[] payload) {
        try {
            userSessions.get(user.uid()).sendMessage(new TextMessage(payload));
        } catch (IOException e) {
            //TODO report back to client? retry?
            log.warn("Message to user {} couldn't be delivered. Reason:\n{}", user.uid(), e.toString());
        }
    }
}
