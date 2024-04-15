package com.playground.games.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "question", schema = "public")
public class Question {

    @Id
    @UuidGenerator
    @Column(name = "question_id", unique = true)
    private UUID id;

    @Column(name = "question")
    private String question;

    @Column(name = "correctAnswer")
    private String correctAnswer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Answer> answers;
}
