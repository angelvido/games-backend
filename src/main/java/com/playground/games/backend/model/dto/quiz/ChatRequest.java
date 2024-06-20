package com.playground.games.backend.model.dto.quiz;

import java.util.List;

public record ChatRequest(
        String model,
        List<Message> messages,
        int n,
        double temperature,
        ResponseFormat response_format

) {
    public ChatRequest(String model, List<Message> messages, ResponseFormat responseFormat) {
        this(model, messages, 1, 1, responseFormat);
    }
}
