package com.playground.games.backend.model.dto;

import jakarta.validation.constraints.NotBlank;

public record StatsDTO(
        @NotBlank(message = "Username cannot be blank")
        String username,
        @NotBlank(message = "Games played cannot be blank")
        int games_played,
        @NotBlank(message = "Correct answers cannot be blank")
        int correct_answers
) {
}
