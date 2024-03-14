package com.playground.games.backend.exception;

public class AccessDeniedException  extends RuntimeException{

    public AccessDeniedException(String message) {
        super(message);
    }
}
