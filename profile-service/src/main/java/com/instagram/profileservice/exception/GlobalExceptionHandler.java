package com.instagram.profileservice.exception;

import com.instagram.exception.TokenNotFoundException;
import com.instagram.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ " + ex.getMessage());
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<String> handleTokenNotFound(TokenNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Internal error: " + ex.getMessage());
    }
}

