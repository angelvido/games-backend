package com.playground.games.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.playground.games.backend.model.entity"})
@EnableJpaRepositories(basePackages = {"com.playground.games.backend.repository"})
public class PlaygroundGamesBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlaygroundGamesBackendApplication.class, args);
	}

}
