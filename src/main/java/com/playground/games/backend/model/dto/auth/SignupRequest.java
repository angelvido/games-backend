package com.playground.games.backend.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "El nombre de usuario no puede estar vacío")
        String username,

        @NotBlank(message = "El nombre no puede estar vacío")
        String name,

        @NotBlank(message = "Los apellidos no pueden estar vacíos")
        String lastname,

        @Email(message = "Formato de email inválido")
        @NotBlank(message = "El email no puede estar vacío")
        String email,

        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 6, max = 20, message = "La contraseña debe tener un mínimo de 6 carácteres y un máximo de 20")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[@#$%^&+=])(?=.*[A-Z]).{6,20}$", message = "La contraseña no cumple con los requisitos de seguridad")
        String password) {
}
