package com.playground.games.backend.repository;

import com.playground.games.backend.model.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
    @Query("SELECT q FROM Question q ORDER BY q.id DESC LIMIT 20")
    List<Question> findTop20Questions();
}
