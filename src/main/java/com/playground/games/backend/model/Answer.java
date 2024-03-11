package com.playground.games.backend.model;

import lombok.Builder;
import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Builder
@Entity
public class Answer {

    @Id
    private String id;

    private String text;

    public Answer(String id, String text) {
        this.id = id;
        this.text = text;
    }
}
