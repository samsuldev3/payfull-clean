package com.clickfud.fudadmin.services;

import com.clickfud.fudadmin.config.PaymentConfig;
import com.clickfud.fudadmin.dtos.VerifyPaymentDTO;
import com.clickfud.fudadmin.utils.SignatureUtil;

import com.google.cloud.firestore.Firestore;      // ✅ CORRECT
import com.google.cloud.firestore.FieldValue;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentConfig cfg;
    private final Firestore db;   // ✅ THIS FIXES EVERYTHING

 public Map<String, Object> createRazorpayOrder(String internalOrderId, int amount) throws Exception {

        RazorpayClient client = new RazorpayClient(cfg.getKey(), cfg.getSecret());

        JSONObject obj = new JSONObject();
        obj.put("amount", amount * 100);
        obj.put("currency", "INR");
        obj.put("receipt", internalOrderId);

        Order rpOrder = client.orders.create(obj);

        Map<String, Object> pay = new HashMap<>();
        pay.put("orderId", internalOrderId);
        pay.put("razorpayOrderId", rpOrder.get("id"));
        pay.put("amount", amount);
        pay.put("status", "CREATED");
        pay.put("createdAt", FieldValue.serverTimestamp());

        db.collection("payments")
                .document(internalOrderId)
                .set(pay);

        // ✅ RESPONSE FOR ANDROID
        Map<String, Object> response = new HashMap<>();
        response.put("razorpayOrderId", rpOrder.get("id"));
        response.put("amount", rpOrder.get("amount"));
        response.put("currency", rpOrder.get("currency"));

        return response;
    }


    public void verifyAndMarkSuccess(VerifyPaymentDTO dto) throws Exception {

        boolean ok = SignatureUtil.verify(
                dto.getRazorpay_order_id(),
                dto.getRazorpay_payment_id(),
                dto.getRazorpay_signature(),
                cfg.getSecret()
        );

        if (!ok) throw new RuntimeException("Invalid signature");

        Map<String, Object> upd = new HashMap<>();
        upd.put("status", "SUCCESS");
        upd.put("razorpayPaymentId", dto.getRazorpay_payment_id());
        upd.put("verifiedAt", FieldValue.serverTimestamp());

        db.collection("payments")
                .document(dto.getOrderId())
                .update(upd);
    }
}
