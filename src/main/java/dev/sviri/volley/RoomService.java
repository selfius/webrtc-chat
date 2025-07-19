package dev.sviri.volley;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class RoomService {

    private final Map<UUID, Room> rooms = new HashMap<>();

    public Room createRoom(User initiator) {
        Room room = new Room(initiator);
        rooms.put(room.uuid(), room);
        return room;
    }

    public Room findRoom(UUID roomId) {
        return rooms.get(roomId);
    }
}
