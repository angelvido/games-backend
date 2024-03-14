package com.playground.games.backend.controller;

import com.playground.games.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @GetMapping("/{userId}")
//    public ResponseEntity<User> updateUser(@PathVariable String userId, @RequestBody User updateUser) {
//        // TODO Aquí debería llamar al servicio para actualizar los datos de usuario
//    }
}
