package dev.sviri.chat.room;

import dev.sviri.chat.user.User;

import java.util.UUID;

public interface RoomService {
    Room createRoom(User initiator);

    Room findRoom(UUID roomId);

    void updateRoom(Room room);
}
