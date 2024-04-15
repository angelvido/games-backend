package com.playground.games.backend.model.dto;

import jakarta.validation.constraints.NotBlank;

public record AnswerDTO(
        @NotBlank(message = "Letter cannot be null.")
        String letter,
        @NotBlank(message = "Text cannot be null.")
        String text) {
}
