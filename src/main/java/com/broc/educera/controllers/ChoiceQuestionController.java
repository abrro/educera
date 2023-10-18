package com.broc.educera.controllers;

import com.broc.educera.entities.ChoiceQuestion;
import com.broc.educera.entities.ChoiceQuestionAnswer;
import com.broc.educera.entities.ChoiceQuestionType;
import com.broc.educera.entities.Quiz;
import com.broc.educera.services.ChoiceQuestionService;
import com.broc.educera.services.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/choicequestion")
public class ChoiceQuestionController {

    private final QuizService quizService;
    private final ChoiceQuestionService choiceQuestionService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getChoiceQuestion(@PathVariable Long id) {
        ChoiceQuestion choiceQuestion = choiceQuestionService.findById(id).orElseThrow();
        return ResponseEntity.ok().body(choiceQuestion);
    }

    //na odredjeni kviz
    @PostMapping("/quiz/{id}")
    public ResponseEntity<?> addChoiceQuestion(@PathVariable Long id, @RequestBody @Valid ChoiceQuestion choiceQuestion) {
        Quiz quiz = quizService.findById(id).orElseThrow();
        //da li mora novi da se pravi ili moze samo set
        if(choiceQuestion.getChoiceQuestionType().equals(ChoiceQuestionType.SINGLE)) {
            if(choiceQuestion.getChoiceQuestionAnswers().stream().map(ChoiceQuestionAnswer::getPercentageValue)
                    .noneMatch(value -> value.equals(100))) {
                //neki drugi error stavi
                return ResponseEntity.internalServerError().build();
            }
        }
        if(choiceQuestion.getChoiceQuestionType().equals(ChoiceQuestionType.MULTI)) {
//            if(choiceQuestion.getChoiceQuestionAnswers().stream().map(ChoiceQuestionAnswer::getPercentageValue)
//                    .filter(value -> value > 0).mapToInt(Integer::intValue).sum() != 100) {
//                return ResponseEntity.internalServerError().build();
//            }
            //bolje ovako
            if(choiceQuestion.getChoiceQuestionAnswers().stream().mapToInt(ChoiceQuestionAnswer::getPercentageValue)
                    .filter(value -> value > 0).sum() != 100) {
                return ResponseEntity.internalServerError().build();
            }
        }

        //mozda treba pomeriti u setter provere??
        choiceQuestion.setQuiz(quiz);
        return ResponseEntity.ok().body(choiceQuestionService.save(choiceQuestion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editChoiceQuestion(@PathVariable Long id, @RequestBody @Valid ChoiceQuestion newChoiceQuestion) {
        ChoiceQuestion choiceQuestion = choiceQuestionService.findById(id).orElseThrow();

        if(newChoiceQuestion.getChoiceQuestionType().equals(ChoiceQuestionType.SINGLE)) {
            if(choiceQuestion.getChoiceQuestionAnswers().stream().map(ChoiceQuestionAnswer::getPercentageValue)
                    .noneMatch(value -> value.equals(100))) {
                //neki drugi error stavi
                return ResponseEntity.internalServerError().build();
            }
        }
        if(newChoiceQuestion.getChoiceQuestionType().equals(ChoiceQuestionType.MULTI)) {
//            if(choiceQuestion.getChoiceQuestionAnswers().stream().map(ChoiceQuestionAnswer::getPercentageValue)
//                    .filter(value -> value > 0).mapToInt(Integer::intValue).sum() != 100) {
//                return ResponseEntity.internalServerError().build();
//            }
            //bolje ovako
            if(choiceQuestion.getChoiceQuestionAnswers().stream().mapToInt(ChoiceQuestionAnswer::getPercentageValue)
                    .filter(value -> value > 0).sum() != 100) {
                return ResponseEntity.internalServerError().build();
            }
        }

        choiceQuestion.setTitle(newChoiceQuestion.getTitle());
        choiceQuestion.setMaximumPoints(newChoiceQuestion.getMaximumPoints());
        choiceQuestion.setChoiceQuestionAnswers(newChoiceQuestion.getChoiceQuestionAnswers());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeChoiceQuestion(@PathVariable Long id) {
        ChoiceQuestion choiceQuestion =  choiceQuestionService.findById(id).orElseThrow();
        choiceQuestionService.deleteById(choiceQuestion.getId());
        return ResponseEntity.ok().build();
    }

}
