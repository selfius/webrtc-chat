package dev.sviri.chat;

import java.util.UUID;

public class Room {
    private final User initiator;

    private final UUID uuid;

    private User follower;

    Room(User initiator) {
        this.initiator = initiator;
        this.uuid = UUID.randomUUID();
    }

    Room(UUID roomId, UUID initiatorId, UUID followerId) {
        this.uuid = roomId;
        this.initiator = new User(initiatorId);
        this.follower = new User(followerId);
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
