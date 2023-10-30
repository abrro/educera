package com.broc.educera.controllers;

import com.broc.educera.dtos.requests.QuestionResponseRequest;
import com.broc.educera.dtos.responses.QuizEnterResponse;
import com.broc.educera.entities.*;
import com.broc.educera.services.CourseEnrollmentService;
import com.broc.educera.services.QuestionService;
import com.broc.educera.services.QuizParticipationService;
import com.broc.educera.services.QuizService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quizzing")
public class QuizParticipationController {

    private final QuizService quizService;
    private final QuizParticipationService quizParticipationService;
    private final QuestionService questionService;
    private final CourseEnrollmentService courseEnrollmentService;

    @GetMapping("/enter/{quiz_id}")
    public ResponseEntity<?> enterQuiz(@PathVariable Long quiz_id, @AuthenticationPrincipal User currentUser) {
        if (currentUser.getUserType().equals(UserType.STUDENT)) {
            Quiz quiz = quizService.findById(quiz_id).orElseThrow();
            courseEnrollmentService.findByStudentIdAndCourseId(currentUser.getId(), quiz.getCourse().getId()).orElseThrow();
            if (quiz.getQuizStatus().equals(QuizStatus.OPEN)) {
                Optional<QuizParticipation> qp =
                        quizParticipationService.findByStudentIdAndQuizId(currentUser.getId(), quiz.getId());
                if (qp.isPresent()) {
                    QuizParticipation qPart = qp.get();
                    if(qPart.getQuizParticipationState().equals(QuizParticipationState.ACCESSED)) {
                        List<Question> questions = questionService.findAllByQuizId(quiz.getId());
                        QuizEnterResponse quizEnterResponse = new QuizEnterResponse(
                                "You have already started this quiz. Time left: ",
                                questions, qPart, quiz);
                        return ResponseEntity.ok().body(quizEnterResponse);
                    } else if (qPart.getQuizParticipationState().equals(QuizParticipationState.SUBMITTED)){
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have already submitted your answers.Go view results.");
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                } else {
                    List<Question> questions = questionService.findAllByQuizId(quiz.getId());
                    QuizParticipation newQuizParticipation = QuizParticipation.builder()
                            .accessTime(LocalDateTime.now())
                            .quiz(quiz)
                            .student(currentUser)
                            .quizParticipationState(QuizParticipationState.ACCESSED)
                            .totalPoints(null)
                            .submissionTime(null)
                            .build();
                    newQuizParticipation = quizParticipationService.save(newQuizParticipation);
                    QuizEnterResponse quizEnterResponse = new QuizEnterResponse(
                            "Entered quiz - Time left: ",
                            questions, newQuizParticipation, quiz);
                    return ResponseEntity.ok().body(quizEnterResponse);
                }
            } else if (quiz.getQuizStatus().equals(QuizStatus.CLOSED)) {
                Optional<QuizParticipation> qp =
                        quizParticipationService.findByStudentIdAndQuizId(currentUser.getId(), quiz_id);
                if (qp.isPresent()) {
                    QuizParticipation qPart = qp.get();
                    List<Question> questions = questionService.findAllByQuizId(quiz.getId());
                    QuizEnterResponse quizEnterResponse = new QuizEnterResponse(
                            "You have already started this quiz on time, even though its not accessible anymore. Time left: ",
                            questions, qPart, quiz);
                    return ResponseEntity.ok().body(quizEnterResponse);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("This quiz is closed on:" + quiz.getAccessibleUntil());
                }
            }else if (quiz.getQuizStatus().equals(QuizStatus.SCHEDULED)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("This quiz is set to start on:" + quiz.getStartTime());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping(value = "/submit/{quiz_id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> submitQuizAnswers(@PathVariable Long quiz_id,
                                               @RequestBody List<QuestionResponseRequest> responses,
                                               @AuthenticationPrincipal User currentUser) {
        if (currentUser.getUserType().equals(UserType.STUDENT)) {
            Quiz quiz = quizService.findById(quiz_id).orElseThrow();
            Optional<QuizParticipation> qpOptional =
                    quizParticipationService.findByStudentIdAndQuizId(currentUser.getId(), quiz.getId());
            if (qpOptional.isPresent()) {
                QuizParticipation qp = qpOptional.get();
                if(qp.getQuizParticipationState().equals(QuizParticipationState.SUBMITTED)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Already submitted the answers for this quiz.");
                } else if(qp.getQuizParticipationState().equals(QuizParticipationState.ACCESSED)){
                    long ontime = ChronoUnit.SECONDS.between(LocalDateTime.now(), qp.getAccessTime().plusMinutes(quiz.getDuration()));
                    System.out.println(LocalDateTime.now());
                    System.out.println(qp.getAccessTime().plusMinutes(quiz.getDuration()));
                    if(ontime > -10) {
                        try {
                            QuizParticipation qpRes = this.quizParticipationService.calculateResults(responses, quiz, qp);
                            return ResponseEntity.ok().body(qpRes);
                        } catch (Exception e) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
                        }
                    } else {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot submit because submission is late by (seconds):" + ontime);
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot send answers before entering the quiz.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/results/{quiz_id}")
    public ResponseEntity<?> getQuestionResponses(@PathVariable Long quiz_id, @AuthenticationPrincipal User currentUser) {
        if (currentUser.getUserType().equals(UserType.STUDENT)) {
            Quiz quiz = quizService.findById(quiz_id).orElseThrow();
            Optional<QuizParticipation> qpOptional =
                    quizParticipationService.findByStudentIdAndQuizId(currentUser.getId(), quiz.getId());
            if (qpOptional.isPresent()) {
                QuizParticipation qp = qpOptional.get();
                if(qp.getQuizParticipationState().equals(QuizParticipationState.SUBMITTED)) {
                    return ResponseEntity.ok().body(qp);
                } else if(qp.getQuizParticipationState().equals(QuizParticipationState.ACCESSED)){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You must first submit your responses for this quiz.");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You haven't entered this quiz yet. Enter quiz and submit responses");
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}

