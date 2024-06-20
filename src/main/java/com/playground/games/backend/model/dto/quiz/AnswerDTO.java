package com.playground.games.backend.model.dto.quiz;

import jakarta.validation.constraints.NotBlank;

public record AnswerDTO(
        @NotBlank(message = "La letra no puede estar en blanco")
        String letter,
        @NotBlank(message = "El texto no puede estar en blanco")
        String text) {
}
