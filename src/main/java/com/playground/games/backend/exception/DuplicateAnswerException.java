package com.playground.games.backend.exception;

public class DuplicateAnswerException extends RuntimeException {
    public DuplicateAnswerException(String message) {
        super(message);
    }
}
