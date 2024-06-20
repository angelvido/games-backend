package com.playground.games.backend.model.dto.quiz;

import com.playground.games.backend.model.dto.quiz.AnswerDTO;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record QuestionDTO(
        @NotBlank(message = "La pregunta no puede estar en blanco")
        String question,
        @NotBlank(message = "La pregunta tiene que tener una respuesta correcta")
        String correctAnswer,
        List<AnswerDTO> answers) {
}
