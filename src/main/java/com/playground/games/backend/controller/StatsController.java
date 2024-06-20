package com.playground.games.backend.controller;

import com.playground.games.backend.model.dto.stats.AddStatRequest;
import com.playground.games.backend.model.dto.stats.StatsDTO;
import com.playground.games.backend.model.entity.User;
import com.playground.games.backend.security.jwt.JwtProvider;
import com.playground.games.backend.service.StatsService;
import com.playground.games.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/stats")
public class StatsController {

    private final UserService userService;
    private final StatsService statsService;

    @Autowired
    public StatsController(UserService userService, StatsService statsService) {
        this.userService = userService;
        this.statsService = statsService;
    }

    @PostMapping("/addStat")
    public ResponseEntity<Void> addStat(@RequestHeader("Authorization") String token, @RequestBody AddStatRequest request) {
        String username = JwtProvider.extractUsername(token.replace("Bearer ", ""));
        User user = userService.findByUsername(username);

        if (user != null) {
            statsService.addStat(user, request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getStats")
    public ResponseEntity<List<StatsDTO>> getStats() {
        List<StatsDTO> allStats = statsService.getAllStats();
        return ResponseEntity.status(HttpStatus.OK).body(allStats);
    }
}
