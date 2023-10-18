package com.broc.educera.controllers;

import com.broc.educera.dtos.QuizParticipationRequest;
import com.broc.educera.entities.*;
import com.broc.educera.services.QuizParticipationService;
import com.broc.educera.services.QuizService;
import com.broc.educera.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quizengine")
public class QuizParticipationController {

    private final QuizService quizService;
    private final UserService userService;
    private final QuizParticipationService quizParticipationService;

    //student takes quiz, student id vadis is authprincipal
    //transakcija?
    @PostMapping("/start/{quiz_id}")
    public ResponseEntity<?> startQuiz(@PathVariable Long quiz_id, Long student_id){
        LocalDateTime accessTime = LocalDateTime.now();
        Quiz quiz = quizService.findById(quiz_id).orElseThrow();
        Optional<QuizParticipation> quizParticipation = quizParticipationService.findByStudentIdAndQuizId(student_id, quiz_id);
        if(quiz.getQuizStatus().equals(QuizStatus.OPEN)) {
            //da li su uopste potrebni enumi...? mozda da jer moze da ponovo pozove start kviz?
            if (quizParticipation.isPresent()) {
                if (quizParticipation.get().getQuizParticipationState().equals(QuizParticipationState.ACCESSED)) {
                    //vec je pristupio, vrati response dto je u toku njegov kviz i da moze da posalje odgovore...moze da vrati preostalo vreme?
                    return ResponseEntity.ok().build();
                } else if (quizParticipation.get().getQuizParticipationState().equals(QuizParticipationState.SUBMITTED)) {
                    //vrati da je vec uradio kviz
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            //principal, provera da li je u pitanju student
            User student = userService.findById(student_id).orElseThrow();
            if (quiz.getQuizStatus().equals(QuizStatus.OPEN) && accessTime.isBefore(quiz.getAccessibleUntil())) {
                QuizParticipation newQuizParticipation = QuizParticipation.builder()
                        .accessTime(accessTime)
                        .quiz(quiz)
                        .student(student)
                        .quizParticipationState(QuizParticipationState.ACCESSED)
                        .build();
                quizParticipationService.save(newQuizParticipation);
                //vraca kviz
                return ResponseEntity.ok().body(quiz);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else if (quiz.getQuizStatus().equals(QuizStatus.CLOSED)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("body");
        } else if (quiz.getQuizStatus().equals(QuizStatus.PREPARED)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("body");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("body");
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitQuizAnswers(@RequestBody QuizParticipation quizParticipation) {
        quizParticipation.setQuizParticipationState(QuizParticipationState.SUBMITTED);
        quizParticipationService.save(quizParticipation);
        return ResponseEntity.ok().build();
    }

}
