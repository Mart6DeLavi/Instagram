package com.instagram.paymentservice.controller;

import com.instagram.paymentservice.service.PaymentTransactionService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/webhook")
public class StripeWebhookController {


    private final String endpointSecret;
    private final PaymentTransactionService transactionService;

    public StripeWebhookController(@Value("${stripe.webhook-secret}") String endpointSecret,
                                   PaymentTransactionService transactionService) {
        this.endpointSecret = endpointSecret;
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload,
                                                    @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            log.info("📩 Received event type: {}", event.getType());

            if ("payment_intent.succeeded".equals(event.getType()) ||
                    "payment_intent.payment_failed".equals(event.getType())) {

                PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElseThrow();
                log.info("🔔 Handling webhook for intentId: {}", intent.getId());
                transactionService.updateStatusFromWebhook(intent);
            }

            return ResponseEntity.ok("Received");
        } catch (SignatureVerificationException e) {
            log.error("❌ Invalid webhook signature: {}", e.getMessage());
            return ResponseEntity.status(400).body("Invalid signature");
        } catch (Exception e) {
            log.error("❌ Webhook processing failed", e);
            return ResponseEntity.status(500).body("Webhook processing failed");
        }
    }
}