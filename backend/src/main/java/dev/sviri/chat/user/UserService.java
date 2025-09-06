package dev.sviri.chat.user;

import org.springframework.web.socket.WebSocketSession;

public interface UserService {
    void bindUser(User user, WebSocketSession webSocketSession);

    void messageUser(User user, byte[] payload);

    default String usersInbox(User user) {
        return String.format("user:%s", user.uid());
    }
}
