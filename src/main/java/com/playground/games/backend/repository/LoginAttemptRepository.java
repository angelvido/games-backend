package com.playground.games.backend.repository;

import com.playground.games.backend.model.entity.LoginAttempt;
import com.playground.games.backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
    List<LoginAttempt> findByUser(User user);
}
