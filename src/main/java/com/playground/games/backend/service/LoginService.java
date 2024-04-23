package com.playground.games.backend.service;

import com.playground.games.backend.model.entity.LoginAttempt;
import com.playground.games.backend.model.entity.User;
import com.playground.games.backend.repository.LoginAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LoginService {

    private final LoginAttemptRepository loginAttemptRepository;

    public LoginService(LoginAttemptRepository loginAttemptRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
    }

    @Transactional
    public void addLoginAttempt(User user, boolean success) {
        LoginAttempt loginAttempt = LoginAttempt.builder()
                .user(user)
                .success(success)
                .createdAt(LocalDateTime.now())
                .build();
        loginAttemptRepository.save(loginAttempt);
    }

    public List<LoginAttempt> findRecentLoginAttempts(User user) {
        return loginAttemptRepository.findByUser(user);
    }
}
