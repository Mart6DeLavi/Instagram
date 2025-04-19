package com.instagram.profileservice.config;

import com.instagram.exceptionhandler.FeignCustomErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignCustomErrorDecoder();
    }
}
