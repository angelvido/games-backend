package com.playground.games.backend.controller;

import com.playground.games.backend.model.entity.User;
import com.playground.games.backend.security.jwt.JwtProvider;
import com.playground.games.backend.model.dto.quiz.QuestionDTO;
import com.playground.games.backend.service.QuizService;
import com.playground.games.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/question")
public class QuizController {

    private final QuizService quizService;
    private final UserService userService;

    @Autowired
    public QuizController(QuizService quizService, UserService userService) {
        this.quizService = quizService;
        this.userService = userService;
    }

    @PostMapping("/postQuestion")
    public ResponseEntity<Void> postQuestion(@RequestHeader("Authorization") String token, @RequestBody QuestionDTO request) {
        String username = JwtProvider.extractUsername(token.replace("Bearer ", ""));
        User user = userService.findByUsername(username);

        if (user != null) {
            quizService.registerQuestion(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getQuestion")
    public ResponseEntity<QuestionDTO> getQuestion(@RequestHeader("Authorization") String token, @RequestParam String topic) {
        String username = JwtProvider.extractUsername(token.replace("Bearer ", ""));
        User user = userService.findByUsername(username);
        if (user != null) {
            String quizzesTopic = "";
            switch (topic) {
                case "SPO":
                    quizzesTopic = "deportes";
                    break;
                case "SCI":
                    quizzesTopic = "ciencia";
                    break;
                case "TECH":
                    quizzesTopic = "tecnología";
                    break;
                case "HIS":
                    quizzesTopic = "historia";
                    break;
                case "ART":
                    quizzesTopic = "arte y literatura";
                    break;
                case "GEO":
                    quizzesTopic = "geografía";
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            try {
                QuestionDTO questionDTO = quizService.generateQuestion(quizzesTopic);
                quizService.registerQuestion(questionDTO);
                return ResponseEntity.ok().body(questionDTO);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
