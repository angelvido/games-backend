package com.playground.games.backend.model.dto.embedding;

import java.util.List;

public record Embedding(
        String object,
        int index,
        List<Float> embedding
) {
}
