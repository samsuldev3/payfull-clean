package com.clickfud.fudadmin.dtos;

import lombok.Data;

@Data
public class VerifyPaymentDTO {
    private String razorpay_order_id;
    private String razorpay_payment_id;
    private String razorpay_signature;
    private String orderId; // your internal order id
}
