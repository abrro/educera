package com.broc.educera.services;

import com.broc.educera.entities.QuizParticipation;
import com.broc.educera.repositories.QuizParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizParticipationService {

    private final QuizParticipationRepository quizParticipationRepository;

    public Optional<QuizParticipation> findById(Long id) {
        return quizParticipationRepository.findById(id);
    }

    public QuizParticipation save(QuizParticipation quizParticipation) {
        return quizParticipationRepository.save(quizParticipation);
    }

    public Optional<QuizParticipation> findByStudentIdAndQuizId(Long student_id, Long quiz_id){
        return quizParticipationRepository.findByStudent_IdAndQuiz_Id(student_id, quiz_id);
    }

}
