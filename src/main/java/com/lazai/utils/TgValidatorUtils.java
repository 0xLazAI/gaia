package com.lazai.utils;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TgValidatorUtils {

    public static boolean isTelegramAuthValid(Map<String, String> data, String botToken) {
        String checkHash = data.get("hash");
        data.remove("hash");

        List<String> fields = data.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .sorted()
                .collect(Collectors.toList());

        String dataCheckString = String.join("\n", fields);

        try {
            byte[] secretKey = MessageDigest.getInstance("SHA-256")
                    .digest(botToken.getBytes(StandardCharsets.UTF_8));

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey, "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hashBytes = sha256_HMAC.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));
            String generatedHash = Hex.encodeHexString(hashBytes);

            return generatedHash.equalsIgnoreCase(checkHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
