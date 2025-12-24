package com.clickfud.fudadmin.dtos;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private String orderId;   // your internal order id
    private int amount;       // final amount (INR)
}

