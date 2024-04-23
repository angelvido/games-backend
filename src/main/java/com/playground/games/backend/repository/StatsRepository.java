package com.playground.games.backend.repository;

import com.playground.games.backend.model.entity.Stats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StatsRepository extends JpaRepository<Stats, UUID> {
    Optional<Stats> findByUserUserId(UUID userId);
}
