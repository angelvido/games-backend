package com.playground.games.backend.model.dto.quiz;

import java.util.List;

public record ChatResponse(
        List<Choice> choices
) {
}
