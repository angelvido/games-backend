package com.playground.games.backend.exception;

public class IncorrectAnswerException extends RuntimeException {
    public IncorrectAnswerException(String message) {
        super(message);
    }
}
