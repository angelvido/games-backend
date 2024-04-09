package com.playground.games.backend.controller;

import com.playground.games.backend.entity.LoginAttempt;
import com.playground.games.backend.model.dto.LoginAttemptResponse;
import com.playground.games.backend.model.dto.LoginRequest;
import com.playground.games.backend.model.dto.LoginResponse;
import com.playground.games.backend.model.dto.SignupRequest;
import com.playground.games.backend.service.LoginService;
import com.playground.games.backend.service.UserService;
import com.playground.games.backend.helper.JwtHelper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final LoginService loginService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService, LoginService loginService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.loginService = loginService;
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
            loginService.addLoginAttempt(request.username(), false);
            throw e;
        }

        String token = JwtHelper.generateToken(request.username());
        loginService.addLoginAttempt(request.username(), true);
        return ResponseEntity.ok(new LoginResponse(request.username(), token));
    }

    @GetMapping("/loginAttempts")
    public ResponseEntity<List<LoginAttemptResponse>> loginAttempts(@RequestHeader("Authorization") String token) {
        String username = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        List<LoginAttempt> loginAttempts = loginService.findRecentLoginAttempts(username);
        return ResponseEntity.ok(convertToDTOs(loginAttempts));
    }

    private List<LoginAttemptResponse> convertToDTOs(List<LoginAttempt> loginAttempts) {
        return loginAttempts.stream()
                .map(LoginAttemptResponse::convertToDTO)
                .collect(Collectors.toList());
    }
}
