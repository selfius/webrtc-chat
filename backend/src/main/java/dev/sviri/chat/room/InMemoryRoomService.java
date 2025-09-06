package dev.sviri.chat.room;

import dev.sviri.chat.user.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Profile("default")
public class InMemoryRoomService implements RoomService{
    private final Map<UUID, Room> rooms = new ConcurrentHashMap<>();

    @Override
    public Room createRoom(User initiator) {
        Room room = new Room(initiator);
        rooms.put(room.uuid(), room);
        return room;
    }

    @Override
    public Room findRoom(UUID roomId) {
        return rooms.get(roomId);
    }

    @Override
    public void updateRoom(Room room) {

    }
}
