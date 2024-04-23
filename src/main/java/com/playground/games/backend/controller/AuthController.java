package com.playground.games.backend.controller;

import com.playground.games.backend.model.entity.LoginAttempt;
import com.playground.games.backend.model.entity.User;
import com.playground.games.backend.model.dto.LoginAttemptResponse;
import com.playground.games.backend.model.dto.LoginRequest;
import com.playground.games.backend.model.dto.LoginResponse;
import com.playground.games.backend.model.dto.SignupRequest;
import com.playground.games.backend.repository.UserRepository;
import com.playground.games.backend.service.LoginService;
import com.playground.games.backend.service.UserService;
import com.playground.games.backend.security.jwt.JwtHelper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final LoginService loginService;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService, LoginService loginService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.loginService = loginService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (BadCredentialsException e) {
            User user = userRepository.findByUsername(request.username())
                            .orElseThrow(() -> new RuntimeException("User not found"));
            loginService.addLoginAttempt(user, false);
            throw e;
        }

        String token = JwtHelper.generateToken(request.username());
        User user = userRepository.findByUsername(request.username())
                        .orElseThrow(() -> new RuntimeException("User not found"));
        loginService.addLoginAttempt(user, true);
        return ResponseEntity.ok(new LoginResponse(request.username(), token));
    }

    @GetMapping("/loginAttempts")
    public ResponseEntity<List<LoginAttemptResponse>> loginAttempts(@RequestHeader("Authorization") String token) {
        String username = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<LoginAttempt> loginAttempts = loginService.findRecentLoginAttempts(user);
            return ResponseEntity.ok(convertToDTOs(loginAttempts));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private List<LoginAttemptResponse> convertToDTOs(List<LoginAttempt> loginAttempts) {
        return loginAttempts.stream()
                .map(LoginAttemptResponse::convertToDTO)
                .toList();
    }
}
