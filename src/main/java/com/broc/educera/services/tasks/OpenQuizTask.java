package com.broc.educera.services.tasks;

import com.broc.educera.entities.Quiz;
import com.broc.educera.entities.QuizStatus;
import com.broc.educera.repositories.QuizRepository;

import java.time.*;

public class OpenQuizTask implements Runnable {

    private QuizRepository quizRepository;
    private Long quizId;
    private Instant openTime;

    public OpenQuizTask(QuizRepository quizRepository, Long quizId, Instant openTime) {
        this.quizRepository = quizRepository;
        this.quizId = quizId;
        this.openTime = openTime;
    }

    @Override
    public void run() {
        Quiz quiz = this.quizRepository.findById(this.quizId).orElseThrow();
        if(this.openTime.equals(quiz.getStartTime().atZone(ZoneId.of("Europe/Belgrade")).toInstant())) {
            System.out.println("opening");
            quiz.setQuizStatus(QuizStatus.OPEN);
            this.quizRepository.save(quiz);
        }
    }
}
