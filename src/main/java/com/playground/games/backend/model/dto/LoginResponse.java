package com.playground.games.backend.model.dto;

public record LoginResponse(
        String username,
        String token) {

}