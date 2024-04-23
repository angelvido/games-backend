package com.playground.games.backend.service;

import com.playground.games.backend.model.dto.SignupRequest;
import com.playground.games.backend.model.dto.UserDTO;
import com.playground.games.backend.model.entity.Stats;
import com.playground.games.backend.repository.StatsRepository;
import com.playground.games.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import com.playground.games.backend.model.entity.User;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final StatsRepository statsRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, StatsRepository statsRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.statsRepository = statsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void signup(SignupRequest request) {
        String email = request.email();
        String username = request.username();
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new DuplicateKeyException(String.format("El usuario con el email '%s' ya existe.", email));
        }

        String hashedPassword = passwordEncoder.encode(request.password());
        User user = User.builder()
                .username(username)
                .name(request.name())
                .lastname(request.lastname())
                .email(email)
                .password(hashedPassword)
                .build();
        userRepository.save(user);

        Stats stats = Stats.builder()
                .games_played(0)
                .correct_answers(0)
                .user(user)
                .build();
        statsRepository.save(stats);
    }

    @Transactional
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No se encuentra el usuario nombre de usuario: " + username));
    }

    @Transactional
    public void updateUserMetadata(String username, UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            try {
                user.setUsername(userDTO.username());
                user.setName(userDTO.name());
                user.setLastname(userDTO.lastname());
                user.setEmail(userDTO.email());
                userRepository.save(user);
                logger.info("Datos actualizados correctamente.");
            } catch (DataIntegrityViolationException e) {
                if (e.getMessage().contains("username")) {
                    logger.error("El nombre de usuario ya está en uso.", e);
                } else if (e.getMessage().contains("email")) {
                    logger.error("El correo electrónico ya está en uso.", e);
                } else {
                    logger.error("Error en la integridad de los datos.", e);
                }
            } catch (Exception e) {
                logger.error("Error al actualizar los metadatos de usuario.", e);
            }
        } else {
            logger.error("No se encontró el usuario con el nombre de usuario: {}", username);
        }
    }
}
