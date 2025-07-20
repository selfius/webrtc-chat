package dev.sviri.chat;

import java.util.UUID;

public class User {
    private final UUID uid;

    User() {
        this.uid = UUID.randomUUID();
    }

    public UUID uid() {
        return uid;
    }
}
