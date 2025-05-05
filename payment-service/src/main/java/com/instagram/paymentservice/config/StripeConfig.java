package com.instagram.paymentservice.config;

import com.stripe.Stripe;
import com.stripe.net.RequestOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StripeConfig {

    private final StripeProperties stripeProperties;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeProperties.getSecretKey();
        Stripe.setAppInfo("Instagram", "1.0.0", "http://localhost");
        Stripe.setMaxNetworkRetries(stripeProperties.getMaxNetworkRetries());
    }

    @Bean
    public RequestOptions defaultRequestOptions() {
        return RequestOptions.builder()
                .setApiKey(stripeProperties.getSecretKey())
                .setConnectTimeout(stripeProperties.getConnectTimeout())
                .setReadTimeout(stripeProperties.getReadTimeout())
                .build();
    }
}
