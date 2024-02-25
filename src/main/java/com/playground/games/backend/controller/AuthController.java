package com.playground.games.backend.controller;

import com.playground.games.backend.model.AuthRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest) {
        // TODO Lógica de autenticación aquí
        String username = authRequest.getUsername();
        String password = authRequest.getPassword();

        // TODO Lógica para autenticar al usuario y generar un token, por ejemplo

        // TODO Devuelve el token o un mensaje de éxito
        return "Authentication successful";
    }
}
