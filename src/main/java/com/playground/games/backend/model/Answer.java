package com.playground.games.backend.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Answer {

    @Id
    private String id;

    private String text;

    public Answer(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public Answer() {
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }
}
