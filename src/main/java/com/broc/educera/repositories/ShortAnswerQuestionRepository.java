package com.broc.educera.repositories;

import com.broc.educera.entities.ShortAnswerQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortAnswerQuestionRepository extends JpaRepository<ShortAnswerQuestion, Long> {
}
