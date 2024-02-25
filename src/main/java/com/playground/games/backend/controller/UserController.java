package com.playground.games.backend.controller;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId, @ResponseBody User updateUser) {
        // TODO Aquí debería llamar al servicio para actualizar los datos de usuario
        User updatedUser = userService.updateUser(UUID.fromString(userId), updateUser);
        if (updateUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
