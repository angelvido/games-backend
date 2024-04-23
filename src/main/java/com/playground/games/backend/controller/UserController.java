package com.playground.games.backend.controller;

import com.playground.games.backend.model.dto.UserDTO;
import com.playground.games.backend.model.entity.User;
import com.playground.games.backend.security.jwt.JwtHelper;
import com.playground.games.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/data")
    public ResponseEntity<User> getUserByUsername(@RequestHeader("Authorization") String token) {
        String username = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        User user = userService.findByUsername(username);

        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateUserMetadata(@RequestHeader("Authorization") String token, @RequestBody UserDTO userDTO) {
        String username = JwtHelper.extractUsername(token.replace("Bearer ",""));
        User user = userService.findByUsername(username);

        if (user != null) {
            userService.updateUserMetadata(username, userDTO);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
