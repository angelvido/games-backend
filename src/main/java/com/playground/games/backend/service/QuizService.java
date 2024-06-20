package com.playground.games.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.games.backend.exception.*;
import com.playground.games.backend.model.dto.embedding.EmbeddingRequest;
import com.playground.games.backend.model.dto.embedding.EmbeddingResponse;
import com.playground.games.backend.model.dto.quiz.*;
import com.playground.games.backend.model.entity.Answer;
import com.playground.games.backend.model.entity.Question;
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
    @Value("${openai.model.gpt35}")
    private String modelGpt35;

    @Value("${openai.model.gpt4}")
    private String modelGpt4;

    @Value("${openai.model.gpt4o}")
    private String modelGpt4o;

    @Value("${openai.model.embedding}")
    private String modelEmbedding;

    @Value("${openai.api.chat.url}")
    private String apiChatUrl;

    @Value("${openai.api.embedding.url}")
    private String apiEmbeddingUrl;

    private static final Set<String> STOPWORDS = new HashSet<>(Set.of(
            "a", "ante", "bajo", "cabe", "con", "contra", "de", "desde", "en", "entre", "hacia", "hasta", "para",
            "por", "según", "sin", "so", "sobre", "tras",
            "el", "la", "los", "las", "un", "una", "unos", "unas",
            "mi", "tu", "su", "nuestro", "vuestro", "sus",
            "me", "te", "se", "nos", "os", "le", "les",
            "esto", "ese", "aquel", "esta", "esa", "aquella", "estos", "esos", "aquellos", "estas", "esas", "aquellas",
            "yo", "tú", "él", "ella", "usted", "nosotros", "nosotras", "vosotros", "vosotras", "ellos", "ellas", "ustedes",
            "y", "e", "ni", "o", "u", "pero", "sino", "mas", "aunque", "que",
            "deber", "poder", "querer", "saber", "ser", "estar", "haber", "tener",
            "sería", "estaría", "habría", "tendría", "será", "estará", "habrá", "tendrá",
            "al", "del",
            "sí", "no", "si", "tal", "cuál", "como", "cuando", "donde", "quien", "cual", "cuanto",
            "más", "menos", "tan", "tanto", "tantos", "tanta", "tantas", "mucho", "muchos", "mucha", "muchas",
            "además", "también", "incluso", "solo", "todo", "nada", "apenas", "casi", "justo",
            "aún", "luego", "mientras", "pronto", "tarde", "temprano",
            "aquí", "ahí", "allí", "cerca", "lejos", "fuera", "dentro", "debajo", "delante", "detrás",
            "acerca", "alrededor", "encima", "enfrente", "mediante", "seguido", "separado", "adentro", "afuera", "junto"
    ));

    private static final double SIMILARITY_THRESHOLD = 0.8;

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
                .questionText(request.question())
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
    public QuestionDTO generateQuestion(String topic) {
        String systemMessageContent = """
                Eres un asistente que genera preguntas aleatorias y de tipo test sobre %s, con una única respuesta correcta, sin respuestas duplicadas y con el siguiente formato JSON:
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
                """.formatted(topic);

        String userPrompt = "Genera un pregunta";

        List<Message> messages = new ArrayList<>();

        Message systemMessage = new Message("system", systemMessageContent);
        messages.add(systemMessage);

        Message userMessage = new Message("user", userPrompt);
        messages.add(userMessage);

        ResponseFormat responseFormat = new ResponseFormat("json_object");

        ChatRequest request = new ChatRequest(modelGpt4o, messages, responseFormat);

        ChatResponse response = restTemplate.postForObject(apiChatUrl, request, ChatResponse.class);

        if (response != null) {
            String responseToString = response.toString();
            logger.info(responseToString);

            return processResponse(response);
        } else {
            throw new NullResponseException("La respuesta a la solicitud fue nula.");
        }

    }

    private QuestionDTO processResponse(ChatResponse response) {
        if (response.choices() != null && !response.choices().isEmpty()) {
            String generatedQuestion = response.choices().get(0).message().content();
            QuestionDTO questionDTO = parseQuestion(generatedQuestion);
            validateQuestion(questionDTO);
            return questionDTO;
        } else {
            throw new NullResponseException("No se recibieron opciones válidas de la API.");
        }
    }

    private QuestionDTO parseQuestion(String generatedQuestion) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(generatedQuestion, QuestionDTO.class);
        } catch (JsonProcessingException e) {
            throw new QuestionParseException("Error al procesar la pregunta generada.", e);
        }
    }

    private void validateQuestion(QuestionDTO questionDTO) {
        Map<String, EmbeddingResponse> questionEmbeddingMap = processLast20Questions();

        if (questionEmbeddingMap == null || questionEmbeddingMap.isEmpty()) {
            logger.info("No hay preguntas en la base de datos para validar la similitud semántica.");
        } else {
            checkSemantic(questionDTO, questionEmbeddingMap);
        }
        checkForDuplicateAnswers(questionDTO);
        String correctAnswerText = findCorrectAnswerText(questionDTO);
        validateCorrectAnswer(questionDTO, correctAnswerText);
    }

    private void checkSemantic(QuestionDTO questionDTO, Map<String, EmbeddingResponse> questionEmbeddingMap) {
        EmbeddingResponse newQuestionEmbedding = generateEmbedding(questionDTO.question());

        for (Map.Entry<String, EmbeddingResponse> entry : questionEmbeddingMap.entrySet()) {
            EmbeddingResponse oldEmbedding = entry.getValue();
            double[] newEmbeddingArray = convertToDoubleArray(newQuestionEmbedding.data().get(0).embedding());
            double[] oldEmbeddingArray = convertToDoubleArray(oldEmbedding.data().get(0).embedding());
            double similarity = calculateCosineSimilarity(newEmbeddingArray, oldEmbeddingArray);
            if (similarity > SIMILARITY_THRESHOLD) {
                logger.error("La pregunta '{}' y la pregunta '{}' son muy similares. Siendo {} la tasa de similaridad obtenida y {} la tasa máxima permitida.",
                        questionDTO.question(), entry.getKey(), similarity, SIMILARITY_THRESHOLD);
                throw new SimilarityThresholdExceededException("La nueva pregunta es demasiado similar a una pregunta existente.");
            }
            logger.info("Pregunta nueva: {}", questionDTO.question());
            logger.info("Pregunta antigua: {}", entry.getKey());
            logger.info("Similaridad obtenida: {}", similarity);
            logger.info("Similar máxima: {}", SIMILARITY_THRESHOLD);
        }
    }

    private double[] convertToDoubleArray(List<Float> floatList) {
        double[] doubleArray = new double[floatList.size()];
        for (int i = 0; i < floatList.size(); i++) {
            doubleArray[i] = floatList.get(i);
        }
        return doubleArray;
    }

    private double calculateCosineSimilarity(double[] vector1, double[] vector2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += Math.pow(vector1[i], 2);
            norm2 += Math.pow(vector2[i], 2);
        }
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    private Map<String, EmbeddingResponse> processLast20Questions() {
        List<Question> last20Questions = questionRepository.findTop20Questions();

        if (last20Questions.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, EmbeddingResponse> questionEmbeddingMap = new HashMap<>();
        for (Question question : last20Questions) {
            EmbeddingResponse response = generateEmbedding(question.getQuestionText());
            questionEmbeddingMap.put(question.getQuestionText(), response);
        }
        return questionEmbeddingMap;
    }

    private String processQuestion(String question) {
        String questionText = question;
        questionText = questionText.toLowerCase();
        questionText = questionText.replaceAll("[^a-zA-Zá-úÁ-Ú0-9\\s]", "");

        String[] words = questionText.split("\\s+");
        List<String> filteredWords = new ArrayList<>();
        for (String word : words) {
            if (!STOPWORDS.contains(word)) {
                filteredWords.add(word);
            }
        }
        return String.join(" ", filteredWords);
    }

    private EmbeddingResponse generateEmbedding(String question) {
        try {
            String processedQuestion = processQuestion(question);
            EmbeddingRequest request = new EmbeddingRequest(processedQuestion, modelEmbedding);
            EmbeddingResponse response = restTemplate.postForObject(apiEmbeddingUrl, request, EmbeddingResponse.class);

            if (response == null) {
                throw new NullPointerException("La respuesta del servicio de embedding fue nula.");
            }

            return response;
        } catch (Exception e) {
            throw new EmbeddingGenerationException("Error al generar embedding para la pregunta: " + e.getMessage());
        }
    }

    private void checkForDuplicateAnswers(QuestionDTO questionDTO) {
        List<String> answerTexts = questionDTO.answers().stream()
                .map(AnswerDTO::text)
                .toList();
        Set<String> uniqueAnswerTexts = new HashSet<>(answerTexts);
        if (answerTexts.size() != uniqueAnswerTexts.size()) {
            throw new DuplicateAnswerException("Se encontraron respuestas duplicadas en la pregunta.");
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
        String systemMessageContent = """
                Eres un sistema que valida con "True" o "False" si la pregunta y la respuesta son correctas, introduciendo la pregunta y la respuesta de la siguiente manera:
                Pregunta: La pregunta en cuestión
                Respuesta: La respuesta en cuestión
                """;

        String userPrompt = """
                Pregunta: %s
                Respuesta: %s
                """.formatted(question, correctAnswerText);

        List<Message> messages = new ArrayList<>();
        Message systemMessage = new Message("system", systemMessageContent);
        Message userMessage = new Message("user", userPrompt);
        messages.add(systemMessage);
        messages.add(userMessage);

        ResponseFormat responseFormat = new ResponseFormat("text");

        ChatRequest validationRequest = new ChatRequest(modelGpt4o, messages, responseFormat);
        ChatResponse validationResponse = restTemplate.postForObject(apiChatUrl, validationRequest, ChatResponse.class);
        if (validationResponse != null) {
            String validationResponseToString = validationResponse.toString();
            logger.info(validationResponseToString);

            if (validationResponse.choices() == null || validationResponse.choices().isEmpty()) {
                throw new NullResponseException("No se recibió una respuesta válida de la API para la validación de la respuesta.");
            }

            String validationResult = validationResponse.choices().get(0).message().content();
            if (!Objects.equals(validationResult, "True")) {
                throw new IncorrectAnswerException("La respuesta correcta generada realmente no es correcta.");
            }
        } else {
            throw new NullResponseException("La respuesta a la solicitud de validación fue nula.");
        }

        logger.info("La respuesta correcta fue validada con éxito.");
    }
}
