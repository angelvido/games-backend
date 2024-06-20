package com.playground.games.backend.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "El nombre de usuario no puede estar en blanco")
        String username,

        @NotBlank(message = "La contraseña no puede estar en blanco")
        String password) {

}