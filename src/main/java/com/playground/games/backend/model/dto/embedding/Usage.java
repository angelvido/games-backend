package com.playground.games.backend.model.dto.embedding;

public record Usage(
        int prompt_tokens,
        int total_tokens
) {
}
