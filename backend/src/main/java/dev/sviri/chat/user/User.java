package dev.sviri.chat.user;

import java.util.UUID;

public class User {
    private final UUID uid;

    User() {
        this.uid = UUID.randomUUID();
    }

    public User(UUID uid) {
        this.uid = uid;
    }

    public UUID uid() {
        return uid;
    }
}
