package dev.sviri.chat;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomService {

    private final RedisTemplate<String, String> redisTemplate;

    RoomService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final String ROOM_KEY_PATTERN = "room:%s";

    public Room createRoom(User initiator) {
        Room room = new Room(initiator);
        updateRoom(room);
        return room;
    }

    public Room findRoom(UUID roomId) {
        var roomHash = redisTemplate.<String, String>opsForHash().entries(produceKey(roomId));
        if (roomHash.isEmpty()) {
            return null;
        }
        String follower = roomHash.get("follower");
        return new Room(roomId, UUID.fromString(roomHash.get("initiator")), ObjectUtils.isEmpty(follower) ? null : UUID.fromString(follower));
    }

    public void updateRoom(Room room) {
        redisTemplate.opsForHash().putAll(produceKey(room.uuid()),
                Map.of("initiator", room.initiator().uid(),
                        "follower", Optional.ofNullable(room.follower()).map(user -> user.uid().toString()).orElse("")));
    }

    private static String produceKey(UUID roomId) {
        return String.format(ROOM_KEY_PATTERN, roomId);
    }
}
