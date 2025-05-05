package com.instagram.paymentservice.service;

import com.instagram.paymentservice.entity.PaymentTransaction;
import com.instagram.paymentservice.model.PaymentStatus;
import com.instagram.paymentservice.repository.PaymentTransactionRepository;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentTransactionService {

    private final PaymentTransactionRepository paymentTransactionRepository;

    public void createTransaction(String paymentIntentId, Long userId, long amount, String currency) {
        paymentTransactionRepository.save(PaymentTransaction.builder()
                .paymentIntentId(paymentIntentId)
                .userId(userId)
                .amount(amount)
                .currency(currency)
                .status(PaymentStatus.CREATED)
                .build());
    }

    public void updateStatusFromWebhook(PaymentIntent intent) {
        String intentId = intent.getId();
        log.info("🔎 Searching transaction for intentId: {}", intentId);

        Optional<PaymentTransaction> optional = paymentTransactionRepository.findByPaymentIntentId(intentId);
        if (optional.isEmpty()) {
            log.warn("❌ No transaction found for intentId: {}", intentId);
            return;
        }

        PaymentTransaction tx = optional.get();
        switch (intent.getStatus()) {
            case "succeeded" -> tx.setStatus(PaymentStatus.SUCCEEDED);
            case "requires_payment_method", "canceled", "requires_action" -> {
                tx.setStatus(PaymentStatus.FAILED);
                tx.setFailureReason("Stripe status: " + intent.getStatus());
            }
        }
        paymentTransactionRepository.save(tx);
        log.info("✅ Updated transaction status to {} for intentId: {}", tx.getStatus(), intentId);
    }
}
