package com.playground.games.backend.model.dto;

import java.util.List;

public record ChatResponse(
        List<Choice> choices
) {
}
