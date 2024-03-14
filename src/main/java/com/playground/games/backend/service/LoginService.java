package com.playground.games.backend.service;

import com.playground.games.backend.entity.LoginAttempt;
import com.playground.games.backend.repository.LoginAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LoginService {

    private final LoginAttemptRepository repository;

    public LoginService(LoginAttemptRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void addLoginAttempt(String username, boolean success) {
        LoginAttempt loginAttempt = LoginAttempt.builder()
                .username(username)
                .success(success)
                .createdAt(LocalDateTime.now())
                .build();
        repository.save(loginAttempt);
    }

    public List<LoginAttempt> findRecentLoginAttempts(String username) {
        return repository.findByUsername(username);
    }
}
