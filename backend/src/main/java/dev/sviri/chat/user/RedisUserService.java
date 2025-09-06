package dev.sviri.chat.user;

import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Profile("gcp")
public class RedisUserService implements UserService {

    private final RedisConnection redisConnection;
    private final RedisMessageListenerContainer redisMessageListenerContainer;


    RedisUserService(LettuceConnectionFactory lettuceConnectionFactory, RedisMessageListenerContainer redisMessageListenerContainer) {
        this.redisConnection = lettuceConnectionFactory.getConnection();
        this.redisMessageListenerContainer = redisMessageListenerContainer;
    }

    private final Map<User, WebSocketSession> sessionByUser = new ConcurrentHashMap<>();

    @Override
    public void bindUser(User user, WebSocketSession webSocketSession) {
        sessionByUser.put(user, webSocketSession);
        redisMessageListenerContainer.addMessageListener((message, unused) -> {
            try {
                sessionByUser.get(user).sendMessage(new TextMessage(message.getBody()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, Topic.channel(usersInbox(user)));
    }

    @Override
    public void messageUser(User user, byte[] payload) {
        this.redisConnection.publish(usersInbox(user).getBytes(StandardCharsets.UTF_8), payload);
        //TODO short circuit if that user is connected to the same instance
    }

}
