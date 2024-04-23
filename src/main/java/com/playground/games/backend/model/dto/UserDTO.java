package com.playground.games.backend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDTO(
        @NotBlank(message = "El nombre de usuario no puede estar vacío")
        String username,

        @NotBlank(message = "El nombre no puede estar vacío")
        String name,

        @NotBlank(message = "Los apellidos no pueden estar vacíos")
        String lastname,

        @Email(message = "Formato de email inválido")
        @NotBlank(message = "El email no puede estar vacío")
        String email
) {
}
