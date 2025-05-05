package com.instagram.paymentservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "stripe")
public class StripeProperties {
    private String secretKey;
    private String publicKey;
    private String webhookSecret;
    private String apiVersion;
    private int connectTimeout;
    private int readTimeout;
    private int maxNetworkRetries;
}
