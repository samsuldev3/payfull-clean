package com.clickfud.fudadmin.controller;

import com.clickfud.fudadmin.config.PaymentConfig;

import com.google.cloud.firestore.Firestore; // ✅ CORRECT

import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks/razorpay")
@RequiredArgsConstructor
public class RazorpayWebhookController {

    private final PaymentConfig cfg;
    private final Firestore db; // ✅ FIXED

    @PostMapping
    public ResponseEntity<String> handle(
            @RequestHeader("X-Razorpay-Signature") String sig,
            @RequestBody String payload) throws Exception {

        boolean ok = Utils.verifyWebhookSignature(
                payload,
                sig,
                cfg.getWebhookSecret()
        );

        if (!ok) {
            return ResponseEntity.status(401).body("Invalid signature");
        }

        JSONObject obj = new JSONObject(payload);
        String event = obj.getString("event");

        if ("payment.captured".equals(event)) {
            String rpPaymentId = obj
                    .getJSONObject("payload")
                    .getJSONObject("payment")
                    .getJSONObject("entity")
                    .getString("id");

            // optional: update payment status in Firestore
            // db.collection("payments").whereEqualTo("razorpayPaymentId", rpPaymentId)...
        }

        return ResponseEntity.ok("ok");
    }
}
