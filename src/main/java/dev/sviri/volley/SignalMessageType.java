package dev.sviri.volley;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

enum SignalMessageType {
    INITIATE("initiate"),
    START_SYNC("start_sync"),
    OFFER("offer"),
    ANSWER("answer"),
    ICE_CANDIDATE("ice_candidate");

    @JsonCreator
    SignalMessageType(String name) {
        this.name = name;
    }

    private String name;

    @JsonValue
    public String getName() {
        return name;
    }
}
