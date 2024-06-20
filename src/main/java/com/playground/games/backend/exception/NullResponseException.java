package com.playground.games.backend.exception;

public class NullResponseException extends RuntimeException {
    public NullResponseException(String message) {
        super(message);
    }
}