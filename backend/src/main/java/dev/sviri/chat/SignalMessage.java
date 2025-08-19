package dev.sviri.chat;

import java.util.UUID;

record SignalMessage(
        SignalMessageType type,
        String sdp,
        UUID senderUserId,
        UUID roomId
) {
    SignalMessage(SignalMessageType type, String sdp) {
        this(type, sdp, null, null);
    }
}
