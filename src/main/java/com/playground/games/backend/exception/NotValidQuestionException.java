package com.playground.games.backend.exception;

public class NotValidQuestionException extends RuntimeException {
    public NotValidQuestionException(String message) {
        super(message);
    }
}

