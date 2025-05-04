package com.instagram.searchservice.exception;

public class FailedToIndexPostException extends RuntimeException {
    public FailedToIndexPostException(String message, Throwable cause) {
        super(message, cause);
    }
}
