package dev.sviri.chat.util;

import java.util.Base64;
import java.util.UUID;

public final class UUIDUtil {
    private UUIDUtil() {
    }

    public static String base64Encode(UUID uuid) {
        return Base64.getEncoder().encodeToString(uuidToBytes(uuid));
    }

    public static UUID base64Decode(String encodedUuid) {
        return bytesToUUID(Base64.getDecoder().decode(encodedUuid));
    }


    private static byte[] uuidToBytes(UUID uuid) {
        long low = uuid.getLeastSignificantBits();
        long high = uuid.getMostSignificantBits();
        byte[] result = new byte[8 * 2];

        for (int i = 0; i < 8; i++) {
            result[i] = (byte) low;
            low >>>= 8;
        }
        for (int i = 8; i < 16; i++) {
            result[i] = (byte) high;
            high >>>= 8;
        }
        return result;
    }


    private static UUID bytesToUUID(byte[] bytes) {
        long low = 0;
        long high = 0;

        for (int i = 0; i < 8; i++) {
            low = (low << 8) + bytes[i];
        }
        for (int i = 8; i < 16; i++) {
            high = (high << 8) + bytes[i];
        }
        return new UUID(high, low);
    }
}
