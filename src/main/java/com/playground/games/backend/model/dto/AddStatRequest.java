package com.playground.games.backend.model.dto;

import jakarta.validation.constraints.NotBlank;

public record AddStatRequest(
        @NotBlank(message = "Result of the answer cannot be null.")
        boolean result
) {
}
