package com.broc.educera.controllers;

import com.broc.educera.dtos.ScheduleRequest;
import com.broc.educera.entities.Quiz;
import com.broc.educera.entities.QuizStatus;
import com.broc.educera.services.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quizzes")
public class QuizController {

    private final QuizService quizService;

    @GetMapping
    public ResponseEntity<?> all(@RequestParam("page") Integer pageNumber,
                                 @RequestParam("size") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.unsorted());
        return ResponseEntity.ok().body(quizService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable Long id) {
        Quiz quiz = quizService.findById(id).orElseThrow();
        return ResponseEntity.ok().body(quiz);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@Valid @RequestBody Quiz quiz) {
        Quiz newQuiz = Quiz.builder()
                .name(quiz.getName())
                .rules(quiz.getRules())
                .startTime(quiz.getStartTime())
                .accessibleUntil(quiz.getAccessibleUntil())
                .duration(quiz.getDuration())
                .quizStatus(QuizStatus.PREPARED)
                .build();
        newQuiz = quizService.save(newQuiz);
        return ResponseEntity.ok().body(newQuiz);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Quiz quiz = quizService.findById(id).orElseThrow();
        quizService.deleteById(quiz.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/schedule",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> scheduleQuiz(@Valid @RequestBody ScheduleRequest scheduleRequest) {
        Quiz quiz = quizService.findById(scheduleRequest.getQuiz_id()).orElseThrow();
        quiz.setStartTime(scheduleRequest.getStartTime());
        quiz.setAccessibleUntil(scheduleRequest.getAccessibleUntil());
        quiz.setDuration(scheduleRequest.getDuration());
        quizService.scheduleQuiz(quiz);
        return ResponseEntity.ok().build();
    }

}
