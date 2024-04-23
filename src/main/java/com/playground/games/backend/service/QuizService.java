package com.playground.games.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.games.backend.model.entity.Answer;
import com.playground.games.backend.model.entity.Question;
import com.playground.games.backend.model.dto.AnswerDTO;
import com.playground.games.backend.model.dto.ChatRequest;
import com.playground.games.backend.model.dto.ChatResponse;
import com.playground.games.backend.model.dto.QuestionDTO;
import com.playground.games.backend.repository.AnswerRepository;
import com.playground.games.backend.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional(readOnly = true)
public class QuizService {
    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final Logger logger = LoggerFactory.getLogger(QuizService.class);
    private final RestTemplate restTemplate;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Autowired
    public QuizService(QuestionRepository questionRepository, AnswerRepository answerRepository, @Qualifier("openAiRestTemplate") RestTemplate restTemplate) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public void registerQuestion(QuestionDTO request) {
        Question quiz = Question.builder()
                .question(request.question())
                .correctAnswer(request.correctAnswer())
                .build();
        questionRepository.save(quiz);

        List<Answer> answers = new ArrayList<>();
        for (AnswerDTO answerDTO : request.answers()) {
            Answer answer = Answer.builder()
                    .letter(answerDTO.letter())
                    .text(answerDTO.text())
                    .question(quiz)
                    .build();
            answers.add(answer);
        }
        answerRepository.saveAll(answers);
    }

    @Transactional
    public QuestionDTO generateQuestion() {
        String prompt = """
                Genera una pregunta de cultura general aleatoria en formato tipo test, con una única respuesta correcta y sin respuestas duplicadas. La pregunta debe ser clara y tener una respuesta objetiva ampliamente reconocida. Asegúrate de que en el campo "correctAnswer" se especifique la letra de la respuesta correcta en lugar del texto completo. Evita duplicaciones de respuestas en el campo "text". Utiliza el siguiente formato en JSON:
                {
                  "question": "La pregunta",
                  "correctAnswer": "La letra de la respuesta correcta a la pregunta",
                  "answers": [
                    { "letter": "A", "text": "Primera respuesta" },
                    { "letter": "B", "text": "Segunda respuesta" },
                    { "letter": "C", "text": "Tercera respuesta" },
                    { "letter": "D", "text": "Cuarta respuesta" }
                  ]
                }
                """;
        ChatRequest request = new ChatRequest(model, prompt);

        ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);

        // TODO - Crear nuevas excepciones para los casos en concreto
        if (response != null) {
            String responseToString = response.toString();
            logger.info(responseToString);

            QuestionDTO questionDTO = processResponse(response);

            try {
                validateQuestion(questionDTO);
            } catch (RuntimeException e) {
                throw e;
            }
            return questionDTO;
        } else {
            throw new RuntimeException("La respuesta a la solicitud fue nula.");
        }

    }

    private QuestionDTO processResponse(ChatResponse response) {
        if (response.choices() != null && !response.choices().isEmpty()) {
            String generatedQuestion = response.choices().get(0).message().content();
            QuestionDTO questionDTO = parseQuestion(generatedQuestion);
            validateQuestion(questionDTO);
            return questionDTO;
        } else {
            throw new RuntimeException("No se recibieron opciones válidas de la API.");
        }
    }

    private QuestionDTO parseQuestion(String generatedQuestion) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(generatedQuestion, QuestionDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al procesar la pregunta generada.", e);
        }
    }

    private void validateQuestion(QuestionDTO questionDTO) {
        checkForDuplicateAnswers(questionDTO);
        String correctAnswerText = findCorrectAnswerText(questionDTO);
        validateCorrectAnswer(questionDTO, correctAnswerText);
    }

    private void checkForDuplicateAnswers(QuestionDTO questionDTO) {
        List<String> answerTexts = questionDTO.answers().stream()
                .map(AnswerDTO::text)
                .toList();
        Set<String> uniqueAnswerTexts = new HashSet<>(answerTexts);
        if (answerTexts.size() != uniqueAnswerTexts.size()) {
            throw new RuntimeException("Se encontraron respuestas duplicadas en la pregunta.");
        }
    }

    private String findCorrectAnswerText(QuestionDTO questionDTO) {
        String correctAnswerLetter = questionDTO.correctAnswer();
        return questionDTO.answers().stream()
                .filter(answer -> answer.letter().equals(correctAnswerLetter))
                .findFirst()
                .map(AnswerDTO::text)
                .orElseThrow(() -> new RuntimeException("No se encontró la respuesta correcta en las respuestas."));
    }

    private void validateCorrectAnswer(QuestionDTO questionDTO, String correctAnswerText) {
        String question = questionDTO.question();
        String validationPrompt = "Responde con “True” o “False” en función de si la siguiente pregunta tiene la siguiente respuesta correcta:\n" +
                "Pregunta: " + question + "\n" +
                "Respuesta: " + correctAnswerText;

        ChatRequest validationRequest = new ChatRequest(model, validationPrompt);
        ChatResponse validationResponse = restTemplate.postForObject(apiUrl, validationRequest, ChatResponse.class);
        if (validationResponse != null) {
            String validationResponseToString = validationResponse.toString();
            logger.info(validationResponseToString);

            if (validationResponse.choices() == null || validationResponse.choices().isEmpty()) {
                throw new RuntimeException("No se recibió una respuesta válida de la API para la validación de la respuesta.");
            }

            String validationResult = validationResponse.choices().get(0).message().content();
            if (!Objects.equals(validationResult, "True")) {
                throw new RuntimeException("La respuesta correcta generada realmente no es correcta.");
            }
        } else {
            throw new RuntimeException("La respuesta a la solicitud de validación fue nula.");
        }
    }

}
