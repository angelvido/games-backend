package com.playground.games.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "users", schema = "public")
public class User {
    @Id
    @UuidGenerator
    @Column(name = "user_id", unique = true)
    private UUID userId;
    @Column(name = "username", unique = true)
    private String username;
    @Column(name = "name")
    private String name;
    @Column(name = "lastname")
    private String lastname;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "password")
    @JsonIgnore
    private String password;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<LoginAttempt> loginAttempts;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Stats stats;
    // TODO - Crear atributo booleano para confirmar la verificación de email
}
