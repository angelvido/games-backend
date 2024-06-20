package com.playground.games.backend.exception;

public class SimilarityThresholdExceededException extends RuntimeException {
    public SimilarityThresholdExceededException(String message) {
        super(message);
    }
}
