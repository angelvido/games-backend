package com.playground.games.backend.entity;

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
@Table(name = "answer", schema = "public")
public class Answer {

    @Id
    @UuidGenerator
    @Column(name = "answer_id", unique = true)
    private UUID answer_id;

    @Column(name = "letter")
    private String letter;

    @Column(name = "text", unique = true)
    private String text;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
}
