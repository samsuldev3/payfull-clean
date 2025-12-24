package com.clickfud.fudadmin.utils;


import org.apache.commons.codec.digest.HmacUtils;

public class SignatureUtil {
    public static boolean verify(String orderId, String paymentId,
                                 String signature, String secret) {
        String payload = orderId + "|" + paymentId;
        String generated = HmacUtils.hmacSha256Hex(secret, payload);
        return generated.equals(signature);
    }
}