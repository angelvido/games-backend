package com.playground.games.backend.model.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record QuestionDTO(
        @NotBlank(message = "Question cannot be null.")
        String question,
        @NotBlank(message = "Question need a correct answer.")
        String correctAnswer,
        List<AnswerDTO> answers) {
}
