package dev.sviri.volley;

record SignalMessage(
        SignalMessageType type,
        String sdpPayload
) {
}
