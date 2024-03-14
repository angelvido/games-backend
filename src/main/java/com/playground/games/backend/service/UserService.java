package com.playground.games.backend.service;

import com.playground.games.backend.model.dto.SignupRequest;
import com.playground.games.backend.repository.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.playground.games.backend.entity.User;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void signup(SignupRequest request) {
        String email = request.email();
        String username = request.username();
        Optional<User> existingUser = repository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new DuplicateKeyException(String.format("User with the email address '%s' already exists.", email));
        }

        String hashedPassword = passwordEncoder.encode(request.password());
        User user = User.builder()
                .username(username)
                .name(request.name())
                .lastname(request.lastname())
                .email(email)
                .password(hashedPassword)
                .build();
        repository.save(user);
    }

    // TODO Aquí se debería de implementar un método para actualizar los datos de usuario
}
