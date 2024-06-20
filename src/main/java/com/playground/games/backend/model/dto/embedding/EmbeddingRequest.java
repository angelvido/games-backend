package com.playground.games.backend.model.dto.embedding;

public record EmbeddingRequest(
        String input,
        String model
) {
}
