package com.broc.educera.services;

import com.broc.educera.entities.Quiz;
import com.broc.educera.entities.QuizStatus;
import com.broc.educera.repositories.QuizRepository;
import com.broc.educera.repositories.common.CommonDaoInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizService implements CommonDaoInterface<Quiz, Long> {

    private final QuizRepository quizRepository;
    private final TaskScheduler taskScheduler;

    public Page<Quiz> findAll(Pageable pageable) {
        return quizRepository.findAll(pageable);
    }

    @Override
    public Optional<Quiz> findById(Long id) {
        return quizRepository.findById(id);
    }

    @Override
    public <S extends Quiz> S save(S quiz) {
        return quizRepository.save(quiz);
    }

    @Override
    public void deleteById(Long id) {
        quizRepository.deleteById(id);
    }

    //async metoda koja ce da ukljuci kviz u datom trenutku..tako sto ce da promeni status.
    //da napravi novi poziv da ga ponisti? sleep..posle odredjenog vremena ga stavlja u CLOSED

    @Async
    public void scheduleQuiz(Quiz quiz) {
        taskScheduler.schedule(() -> {
            Quiz q = quizRepository.findById(quiz.getId()).orElseThrow();
            //moze da se pristupa
            q.setQuizStatus(QuizStatus.OPEN);
            quizRepository.save(q);
            try {
                //ovo je trajanje u minutima? promeni
                Thread.sleep(quiz.getDuration() * 60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, Instant.from(quiz.getStartTime()));
    }
}
