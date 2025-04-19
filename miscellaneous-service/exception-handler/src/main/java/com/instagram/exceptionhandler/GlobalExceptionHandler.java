package com.instagram.exceptionhandler;

import com.instagram.dto.SimpleErrorResponse;
import com.instagram.exception.TokenNotFoundException;
import com.instagram.exception.UserNotFoundException;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<SimpleErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new SimpleErrorResponse(LocalDateTime.now(), ex.getMessage()));
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<SimpleErrorResponse> handleTokenNotFound(TokenNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new SimpleErrorResponse(LocalDateTime.now(), ex.getMessage()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<SimpleErrorResponse> handleFeignException(FeignException ex) {
        String content = ex.contentUTF8();
        String message = (content != null && content.contains(":"))
                ? content.split(":", 2)[0]
                : "Unknown Feign error";

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new SimpleErrorResponse(LocalDateTime.now(), "Upstream error: " + message));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<SimpleErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SimpleErrorResponse(LocalDateTime.now(), "Something went wrong: " + ex.getMessage()));
    }
}
