package com.broc.educera.services;

import com.broc.educera.entities.Quiz;
import com.broc.educera.entities.QuizStatus;
import com.broc.educera.repositories.QuizRepository;
import com.broc.educera.repositories.common.CommonDaoInterface;
import com.broc.educera.services.tasks.CloseQuizTask;
import com.broc.educera.services.tasks.OpenQuizTask;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizService implements CommonDaoInterface<Quiz, Long> {

    private final QuizRepository quizRepository;
    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    public Page<Quiz> findAll(Pageable pageable) {
        return quizRepository.findAll(pageable);
    }

    public List<Quiz> findAllByCourseId(Long course_id) {
        return quizRepository.findAllByCourse_Id(course_id);
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

    public void scheduleQuiz(Quiz quiz, Instant time) {
        threadPoolTaskScheduler.schedule(() -> {
            Quiz q = quizRepository.findById(quiz.getId()).orElseThrow();
            q.setQuizStatus(QuizStatus.OPEN);
            quizRepository.save(q);
            try {
                long sleep = ChronoUnit.MILLIS.between(quiz.getStartTime(), quiz.getAccessibleUntil());
                System.out.println("Sleeping for:" + sleep);
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            q = quizRepository.findById(quiz.getId()).orElseThrow();
            q.setQuizStatus(QuizStatus.CLOSED);
            quizRepository.save(q);
        }, time);
    }

    public void scheduleOpenQuiz(Long quizId, Instant openTime) {
        threadPoolTaskScheduler.schedule(new OpenQuizTask(quizRepository, quizId, openTime), openTime);
    }

    public void scheduleCloseQuiz(Long quizId, Instant closeTime) {
        threadPoolTaskScheduler.schedule(new CloseQuizTask(quizRepository, quizId, closeTime), closeTime);
    }
}
