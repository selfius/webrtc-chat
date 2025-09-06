package dev.sviri.chat.room;

import dev.sviri.chat.user.User;
import org.springframework.lang.Nullable;

import java.util.UUID;

public class Room {
    private final User initiator;

    private final UUID uuid;

    private User follower;

    Room(User initiator) {
        this.initiator = initiator;
        this.uuid = UUID.randomUUID();
    }

    Room(UUID roomId, UUID initiatorId, @Nullable UUID followerId) {
        this.uuid = roomId;
        this.initiator = new User(initiatorId);
        this.follower = followerId == null ?  null : new User(followerId);
    }

    public Room setFollower(User follower) {
        this.follower = follower;
        return this;
    }

    public UUID uuid() {
        return this.uuid;
    }

    public User initiator() {
        return initiator;
    }

    public User follower() {
        return follower;
    }
}
