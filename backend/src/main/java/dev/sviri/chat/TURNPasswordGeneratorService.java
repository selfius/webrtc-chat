package dev.sviri.chat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Service
public class TURNPasswordGeneratorService {

    private final SecretKeySpec secretKeySpec;
    private final static String ALGORITHM = "HmacSHA1";

    public TURNPasswordGeneratorService(@Value("${turn-server-secret:'secret'}") String turnServerSecret) {
        secretKeySpec = new SecretKeySpec((turnServerSecret == null ? "" : turnServerSecret).getBytes(), ALGORITHM);
    }

    public String generatePassword() {
        try {
            var hmacSHA1 = Mac.getInstance(ALGORITHM);
            hmacSHA1.init(secretKeySpec);
            String userId = String.valueOf(Instant.now().plus(Duration.ofMinutes(2)).getEpochSecond());
            byte[] password = hmacSHA1.doFinal(userId.getBytes());
            return String.format("%s:%s",userId, new String(Base64.getEncoder().encode(password)));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

}
