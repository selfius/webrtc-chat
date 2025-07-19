package dev.sviri.volley;

import java.util.UUID;

class Room {
    private final User initiator;
    private final UUID uuid;

    Room(User initiator) {
        this.initiator = initiator;
        this.uuid = UUID.randomUUID();
    }

    User follower;


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
