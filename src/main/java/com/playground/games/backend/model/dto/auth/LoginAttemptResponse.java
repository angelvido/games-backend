package com.playground.games.backend.model.dto.auth;

import com.playground.games.backend.model.entity.LoginAttempt;

import java.time.LocalDateTime;

public record LoginAttemptResponse(LocalDateTime createdAt, boolean success) {

    public static LoginAttemptResponse convertToDTO(LoginAttempt loginAttempt) {
        return new LoginAttemptResponse(loginAttempt.getCreatedAt(), loginAttempt.getSuccess());
    }
}
