package com.playground.games.backend.model.dto;

import java.util.ArrayList;
import java.util.List;

public record ChatRequest(
        String model,
        List<Message> messages,
        int n,
        double temperature

) {
    public ChatRequest(String model, String prompt) {
        this(model, new ArrayList<>(List.of(new Message("user", prompt))), 1, 0.7);
    }
}
