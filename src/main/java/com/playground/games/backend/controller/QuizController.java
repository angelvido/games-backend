package com.playground.games.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.games.backend.entity.User;
import com.playground.games.backend.helper.JwtHelper;
import com.playground.games.backend.model.dto.ChatRequest;
import com.playground.games.backend.model.dto.ChatResponse;
import com.playground.games.backend.model.dto.QuestionDTO;
import com.playground.games.backend.service.QuizService;
import com.playground.games.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("api/question")
public class QuizController {

    @Qualifier("openAiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final QuizService quizService;
    private final UserService userService;

    @Autowired
    public QuizController(QuizService quizService, UserService userService) {
        this.quizService = quizService;
        this.userService = userService;
    }

    @PostMapping("/postQuestion")
    public ResponseEntity<Void> postQuestion(@RequestHeader("Authorization") String token, @RequestBody QuestionDTO request) {
        String username = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        User user = userService.findByUsername(username);

        if (user != null) {
            quizService.registerQuestion(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getQuestion")
    public ResponseEntity<QuestionDTO> getQuestion(@RequestHeader("Authorization") String token) throws JsonProcessingException {
        String username = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        User user = userService.findByUsername(username);

        if (user != null) {
            // TODO - GET QUESTION
            // Step 1: Contruir la request a la API de OpenAI
            String prompt = "Genera una pregunta de cultura general aleatoria con formato json con esta estructura: {\n" +
                    "  \"question\": \"¿Cuál es la capital de Francia?\",\n" +
                    "  \"correctAnswer\": \"B\",\n" +
                    "  \"answers\": [\n" +
                    "    { \"letter\": \"A\", \"text\": \"Madrid\" },\n" +
                    "    { \"letter\": \"B\", \"text\": \"París\" },\n" +
                    "    { \"letter\": \"C\", \"text\": \"Londres\" },\n" +
                    "    { \"letter\": \"D\", \"text\": \"Roma\" }\n" +
                    "  ]\n" +
                    "}";
            ChatRequest request = new ChatRequest(model, prompt);

            // Step 2: Realizar la request a la api y manejar bien la respuesta
            ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);
            // Step 3: Guardarlo en base de datos
            // Step 4: Mandar la pregunta generada al cliente
            if (response != null && response.choices() != null && !response.choices().isEmpty()) {
                String generatedQuestion = response.choices().get(0).message().content();
                ObjectMapper objectMapper = new ObjectMapper();
                QuestionDTO questionDTO = objectMapper.readValue(generatedQuestion, QuestionDTO.class);
                quizService.registerQuestion(questionDTO);
                return ResponseEntity.ok(questionDTO);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
