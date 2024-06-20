package com.playground.games.backend.service;

import com.playground.games.backend.model.dto.stats.AddStatRequest;
import com.playground.games.backend.model.dto.stats.StatsDTO;
import com.playground.games.backend.model.entity.Stats;
import com.playground.games.backend.model.entity.User;
import com.playground.games.backend.repository.StatsRepository;
import com.playground.games.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Service
public class StatsService {

    private final StatsRepository statsRepository;
    private final UserRepository userRepository;

    @Autowired
    public StatsService(StatsRepository statsRepository, UserRepository userRepository) {
        this.statsRepository = statsRepository;
        this.userRepository = userRepository;
    }

    public void addStat(User user, AddStatRequest request) {
        Optional<Stats> optionalStats = statsRepository.findByUserUserId(user.getUserId());
        Stats stats = optionalStats.orElseThrow(() -> new RuntimeException("No se encontraron estadísticas para el usuario con ID: " + user.getUserId()));
        if (request.result()) {
            stats.setGamesPlayed(stats.getGamesPlayed() + 1);
            stats.setCorrectAnswers(stats.getCorrectAnswers() + 1);
        } else {
            stats.setGamesPlayed(stats.getGamesPlayed() + 1);
        }
        statsRepository.save(stats);
    }

    public List<StatsDTO> getAllStats() {
        List<Stats> allStats = statsRepository.findAll();
        return allStats.stream()
                .map(stats -> {
                    String username = userRepository.findById(stats.getUser().getUserId())
                            .orElseThrow(() -> new RuntimeException("No se encontró el usuario con ID: " + stats.getUser().getUserId()))
                            .getUsername();
                    return new StatsDTO(username, stats.getGamesPlayed(), stats.getCorrectAnswers());
                })
                .sorted(Comparator.comparingInt(StatsDTO::games_played).reversed())
                .toList();
    }

}
