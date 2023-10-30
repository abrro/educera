package com.broc.educera.services.tasks;

import com.broc.educera.entities.Quiz;
import com.broc.educera.entities.QuizStatus;
import com.broc.educera.repositories.QuizRepository;

import java.time.Instant;
import java.time.ZoneId;

public class CloseQuizTask implements Runnable {

    private QuizRepository quizRepository;
    private Long quizId;
    private Instant closeTime;

    public CloseQuizTask(QuizRepository quizRepository, Long quizId, Instant closeTime) {
        this.quizRepository = quizRepository;
        this.quizId = quizId;
        this.closeTime = closeTime;
    }

    @Override
    public void run() {
        Quiz quiz = this.quizRepository.findById(this.quizId).orElseThrow();
        if(this.closeTime.equals(quiz.getAccessibleUntil().atZone(ZoneId.of("Europe/Belgrade")).toInstant())) {
            quiz.setQuizStatus(QuizStatus.CLOSED);
            this.quizRepository.save(quiz);
        }
    }

}
