package dev.sviri.chat;

record SignalMessage(
        SignalMessageType type,
        String sdp
) {
}
