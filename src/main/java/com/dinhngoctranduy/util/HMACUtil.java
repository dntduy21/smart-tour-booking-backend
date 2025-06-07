package com.dinhngoctranduy.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

public class HMACUtil {
    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo HMAC", e);
        }
    }
}