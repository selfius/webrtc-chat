package dev.sviri.volley;

import java.util.UUID;

class User {
    private final UUID uid;

    User() {
        this.uid = UUID.randomUUID();
    }

    public UUID uid() {
        return uid;
    }
}
