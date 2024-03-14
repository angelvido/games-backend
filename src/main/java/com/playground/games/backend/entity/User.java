package com.playground.games.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "user", schema = "public")
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
    private String password;
}
