package com.playground.games.backend.service;

import com.playground.games.backend.entity.Answer;
import com.playground.games.backend.entity.Question;
import com.playground.games.backend.model.dto.AnswerDTO;
import com.playground.games.backend.model.dto.QuestionDTO;
import com.playground.games.backend.repository.AnswerRepository;
import com.playground.games.backend.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class QuizService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public QuizService(QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
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
}
