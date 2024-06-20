package com.playground.games.backend.model.dto.auth;

public record LoginResponse(
        String username,
        String token) {

}