package com.playground.games.backend.model.dto.stats;

import jakarta.validation.constraints.NotBlank;

public record AddStatRequest(
        @NotBlank(message = "El resultado de la pregunta no puede estar en blanco")
        boolean result
) {
}
