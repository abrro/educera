package com.broc.educera.controllers;

import com.broc.educera.entities.*;
import com.broc.educera.services.ChoiceQuestionService;
import com.broc.educera.services.QuizService;
import com.broc.educera.services.ShortAnswerQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shortanswerquestion")
public class ShortAnswerQuestionController {

    private final QuizService quizService;
    private final ShortAnswerQuestionService shortAnswerQuestionService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getShortAnswerQuestion(@PathVariable Long id){
        ShortAnswerQuestion shortAnswerQuestion = shortAnswerQuestionService.findById(id).orElseThrow();
        return ResponseEntity.ok().body(shortAnswerQuestion);
    }

    @PostMapping("/quiz/{id}")
    public ResponseEntity<?> addShortAnswerQuestion(@PathVariable Long id,
                                                    @RequestBody @Valid ShortAnswerQuestion shortAnswerQuestion){
        Quiz quiz = quizService.findById(id).orElseThrow();
        if(shortAnswerQuestion.getAcceptedWildcards().stream().map(ShortAnswerWildcard::getPercentageValue)
                .noneMatch(value -> value.equals(100))) {
            return ResponseEntity.internalServerError().build();
        }
        shortAnswerQuestion.setQuiz(quiz);
        return ResponseEntity.ok().body(shortAnswerQuestionService.save(shortAnswerQuestion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editShortAnswerQuestion(@PathVariable Long id,
                                                     @RequestBody @Valid ShortAnswerQuestion newShortAnswerQuestion){
        ShortAnswerQuestion shortAnswerQuestion = shortAnswerQuestionService.findById(id).orElseThrow();
        if(newShortAnswerQuestion.getAcceptedWildcards().stream().map(ShortAnswerWildcard::getPercentageValue)
                .noneMatch(value -> value.equals(100))) {
            return ResponseEntity.internalServerError().build();
        }
        shortAnswerQuestion.setTitle(shortAnswerQuestion.getTitle());
        shortAnswerQuestion.setMaximumPoints(newShortAnswerQuestion.getMaximumPoints());
        shortAnswerQuestion.setText(newShortAnswerQuestion.getText());
        shortAnswerQuestion.setAcceptedWildcards(newShortAnswerQuestion.getAcceptedWildcards());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeShortAnswerQuestion(@PathVariable Long id){
        ShortAnswerQuestion shortAnswerQuestion =  shortAnswerQuestionService.findById(id).orElseThrow();
        shortAnswerQuestionService.deleteById(shortAnswerQuestion.getId());
        return ResponseEntity.ok().build();
    }
}
