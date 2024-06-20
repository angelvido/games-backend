package com.playground.games.backend.model.dto.stats;

import jakarta.validation.constraints.NotBlank;

public record StatsDTO(
        @NotBlank(message = "El nombre de usuario no puede estar en blanco")
        String username,
        @NotBlank(message = "Las partidas jugadas no pueden estar en blanco")
        int games_played,
        @NotBlank(message = "Las respuestas correctas no pueden estar en blanco")
        int correct_answers
) {
}
