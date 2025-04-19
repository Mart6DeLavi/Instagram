package com.instagram.exceptionhandler;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FeignCustomErrorDecoder  implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 500 && methodKey.contains("findTokenByUsername")) {
            return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "❌ Token not found for user");
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }
}
