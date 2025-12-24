package com.clickfud.fudadmin.controller;


import com.clickfud.fudadmin.dtos.PaymentRequestDTO;
import com.clickfud.fudadmin.dtos.VerifyPaymentDTO;
import com.clickfud.fudadmin.services.PaymentService;
import com.clickfud.fudadmin.utils.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody PaymentRequestDTO dto) throws Exception {
        return paymentService.createRazorpayOrder(
                dto.getOrderId(),
                dto.getAmount()
        );
    }

    @PostMapping("/verify")
    public ApiResponse<?> verify(@RequestBody VerifyPaymentDTO dto) throws Exception {
        paymentService.verifyAndMarkSuccess(dto);
        return ApiResponse.success("Payment verified");
    }
}
