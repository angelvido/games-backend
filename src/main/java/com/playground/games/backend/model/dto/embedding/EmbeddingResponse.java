package com.playground.games.backend.model.dto.embedding;

import java.util.List;

public record EmbeddingResponse(
        String object,
        List<Embedding> data,
        String model,
        Usage usage
) {
}
