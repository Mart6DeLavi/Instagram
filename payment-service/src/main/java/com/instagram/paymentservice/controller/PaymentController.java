package com.instagram.paymentservice.controller;

import com.instagram.paymentservice.service.PaymentService;
import com.instagram.paymentservice.service.PaymentTransactionService;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentTransactionService transactionService;

    @PostMapping("/create")
    public String createPayment(@RequestParam long userId, @RequestParam long amount, @RequestParam String currency) {
        try {
            PaymentIntent intent = paymentService.createPaymentIntent(amount, currency);
            transactionService.createTransaction(intent.getId(), userId, amount, currency);
            return intent.getClientSecret();
        } catch (Exception e) {
            return "Payment creating error: " + e.getMessage();
        }
    }
}
