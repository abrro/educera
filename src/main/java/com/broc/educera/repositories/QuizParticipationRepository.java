package com.broc.educera.repositories;

import com.broc.educera.entities.QuizParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizParticipationRepository extends JpaRepository<QuizParticipation, Long> {
    Optional<QuizParticipation> findByStudent_IdAndQuiz_Id(Long student_id, Long quiz_id);
}
