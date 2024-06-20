package com.playground.games.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "stats", schema = "public")
public class Stats {
    @Id
    @UuidGenerator
    @Column(name = "stats_id", unique = true)
    private UUID id;
    @Column(name = "games_played")
    int gamesPlayed;
    @Column(name = "correct_answers")
    int correctAnswers;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @JsonIgnore
    private User user;
}
