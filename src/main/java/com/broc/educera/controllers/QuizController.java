package com.broc.educera.controllers;

import com.broc.educera.entities.Course;
import com.broc.educera.entities.Quiz;
import com.broc.educera.entities.QuizStatus;
import com.broc.educera.services.CourseService;
import com.broc.educera.services.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quizzes")
public class QuizController {

    private final QuizService quizService;
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<?> all(@RequestParam("page") Integer pageNumber,
                                 @RequestParam("size") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.unsorted());
        return ResponseEntity.ok().body(quizService.findAll(pageable));
    }

    @GetMapping("/course/{course_id}")
    public ResponseEntity<?> allInCourse(@PathVariable Long course_id) {
        return ResponseEntity.ok().body(quizService.findAllByCourseId(course_id));
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable Long id) {
        Quiz quiz = quizService.findById(id).orElseThrow();
        return ResponseEntity.ok().body(quiz);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@Valid @RequestBody Quiz quiz, @RequestParam Long course_id) {
        Course course = courseService.findById(course_id).orElseThrow();
        //ZonedDateTime zonedDateTime = quiz.getStartTime().atZone(ZoneId.of("Europe/Belgrade"));
        //Instant time = zonedDateTime.toInstant();

        ZonedDateTime openTime = quiz.getStartTime().atZone(ZoneId.of("Europe/Belgrade"));
        ZonedDateTime closeTime = quiz.getAccessibleUntil().atZone(ZoneId.of("Europe/Belgrade"));

        Quiz newQuiz = Quiz.builder()
                .name(quiz.getName())
                .rules(quiz.getRules())
                .startTime(quiz.getStartTime())
                .accessibleUntil(quiz.getAccessibleUntil())
                .duration(quiz.getDuration())
                .quizStatus(QuizStatus.SCHEDULED)
                .maxPoints(0.0)
                .course(course)
                .build();
        newQuiz = quizService.save(newQuiz);
        //quizService.scheduleQuiz(newQuiz, time);
        quizService.scheduleOpenQuiz(newQuiz.getId(), openTime.toInstant());
        quizService.scheduleCloseQuiz(newQuiz.getId(), closeTime.toInstant());
        return ResponseEntity.ok().body(newQuiz);
    }

    @PutMapping(value = "{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> edit(@PathVariable Long id,
                                  @Valid @RequestBody Quiz newQuiz) {
        Quiz quiz = quizService.findById(id).orElseThrow();
        //ZonedDateTime zonedDateTime = newQuiz.getStartTime().atZone(ZoneId.of("Europe/Belgrade"));
        //Instant time = zonedDateTime.toInstant();
        ZonedDateTime openTime = newQuiz.getStartTime().atZone(ZoneId.of("Europe/Belgrade"));
        ZonedDateTime closeTime = newQuiz.getAccessibleUntil().atZone(ZoneId.of("Europe/Belgrade"));

        if(!openTime.equals(quiz.getStartTime().atZone(ZoneId.of("Europe/Belgrade")))) {
            quizService.scheduleOpenQuiz(newQuiz.getId(), openTime.toInstant());
        }
        if(!closeTime.equals(quiz.getAccessibleUntil().atZone(ZoneId.of("Europe/Belgrade")))) {
            quizService.scheduleOpenQuiz(newQuiz.getId(), openTime.toInstant());
        }

        quiz.setName(newQuiz.getName());
        quiz.setRules(newQuiz.getRules());
        quiz.setDuration(newQuiz.getDuration());
        quiz.setStartTime(newQuiz.getStartTime());
        quiz.setAccessibleUntil(newQuiz.getAccessibleUntil());
        quiz.setQuizStatus(QuizStatus.SCHEDULED);
        quizService.save(quiz);
        //quizService.scheduleQuiz(quiz, time);
        return ResponseEntity.ok().body(newQuiz);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Quiz quiz = quizService.findById(id).orElseThrow();
        quizService.deleteById(quiz.getId());
        return ResponseEntity.ok().build();
    }


}
