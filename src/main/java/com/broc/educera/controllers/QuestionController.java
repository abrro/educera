package com.broc.educera.controllers;

import com.broc.educera.entities.*;
import com.broc.educera.services.QuestionService;
import com.broc.educera.services.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final QuizService quizService;

    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable Long id) {
        Question question = questionService.findById(id).orElseThrow();
        return ResponseEntity.ok().body(question);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addQuestion(@RequestParam Long quiz_id,
                                         @RequestBody @Valid Question question,
                                         @AuthenticationPrincipal User currentUser) {
        Quiz quiz = quizService.findById(quiz_id).orElseThrow();
        if(currentUser.getUserType().equals(UserType.TEACHER)
                && currentUser.getId().equals(quiz.getCourse().getTeacher().getId())) {
            if (question.getQuestionType().equals(QuestionType.SINGLE_CHOICE) || question.getQuestionType().equals(QuestionType.SHORT_ANSWER)) {
                if (question.getQuestionAnswers().stream().mapToDouble(QuestionAnswer::getPercentageValue)
                        .noneMatch(value -> value == 1.0)) {
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                            .body("One of the possible answers must give 100% points.");
                }
            }
            if (question.getQuestionType().equals(QuestionType.MULTI_CHOICE)) {
                if (question.getQuestionAnswers().stream().mapToDouble(QuestionAnswer::getPercentageValue)
                        .filter(value -> value > 0).sum() != 1.0) {
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                            .body("Positive percentages must add up to 100%.");
                }
            }

            for(QuestionAnswer answer : question.getQuestionAnswers()) {
                answer.setQuestion(question);
            }
            question.setQuiz(quiz);
            quiz.setMaxPoints(quiz.getMaxPoints() + question.getMaximumPoints());
            return ResponseEntity.ok().body(questionService.save(question));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editQuestion(@PathVariable Long id,
                                          @RequestBody @Valid Question newQuestion,
                                          @AuthenticationPrincipal User currentUser) {
        Question question = questionService.findById(id).orElseThrow();
        //Quiz quiz = quizService.findById(question.getQuiz().getId()).orElseThrow();
        Quiz quiz = question.getQuiz();
        if(currentUser.getUserType().equals(UserType.TEACHER) &&
                currentUser.getId().equals(question.getQuiz().getCourse().getTeacher().getId())) {

            if (newQuestion.getQuestionType().equals(QuestionType.SINGLE_CHOICE) || newQuestion.getQuestionType().equals(QuestionType.SHORT_ANSWER)) {
                if (newQuestion.getQuestionAnswers().stream().mapToDouble(QuestionAnswer::getPercentageValue)
                        .noneMatch(value -> value == 1.0)) {
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                            .body("One of the possible answers must give 100% points.");
                }
            }
            if (newQuestion.getQuestionType().equals(QuestionType.MULTI_CHOICE)) {
                if (newQuestion.getQuestionAnswers().stream().mapToDouble(QuestionAnswer::getPercentageValue)
                        .filter(value -> value > 0).sum() != 1.0) {
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                            .body("Positive percentages must add up to 100%.");
                }
            }

            question.setTitle(newQuestion.getTitle());
            question.setText(newQuestion.getText());
            question.setOrdinalNum(newQuestion.getOrdinalNum());
            question.setMaximumPoints(newQuestion.getMaximumPoints());
            question.setAnswers(newQuestion.getQuestionAnswers());
            question.setQuestionType(newQuestion.getQuestionType());
            if(newQuestion.getQuestionType().equals(QuestionType.SHORT_ANSWER)){
                question.setCaseSensitive(newQuestion.isCaseSensitive());
            }
            for(QuestionAnswer answer : question.getQuestionAnswers()) {
                answer.setQuestion(question);
            }
            questionService.save(question);
            quiz.setMaxPoints(quiz.getMaxPoints() - question.getMaximumPoints() + newQuestion.getMaximumPoints());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/quiz/{quiz_id}")
    public ResponseEntity<?> allByQuizId(@PathVariable Long quiz_id) {
        return ResponseEntity.ok().body(questionService.findAllByQuizId(quiz_id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Question choiceQuestion =  questionService.findById(id).orElseThrow();
        questionService.deleteById(choiceQuestion.getId());
        return ResponseEntity.ok().build();
    }
}
