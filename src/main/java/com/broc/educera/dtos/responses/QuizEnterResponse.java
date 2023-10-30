package com.broc.educera.dtos.responses;


import com.broc.educera.entities.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class QuizEnterResponse {
    private long timeLeftSeconds;
    private String message;
    private List<QuestionResponse> questions;
    private QuizParticipation quizParticipation;

    public QuizEnterResponse(String message, List<Question> questions, QuizParticipation qp, Quiz quiz) {
        this.timeLeftSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), qp.getAccessTime().plusMinutes(quiz.getDuration()));
        this.message = message + this.timeLeftSeconds;
        this.quizParticipation = qp;

        this.questions = new ArrayList<>();
        for(Question q : questions) {

            List<QuestionAnswerResponse> questionAnswerResponseList = new ArrayList<>();
            for(QuestionAnswer qa : q.getQuestionAnswers()) {
                if (qa.getQuestion().getQuestionType().equals(QuestionType.MULTI_CHOICE)
                        || qa.getQuestion().getQuestionType().equals(QuestionType.SINGLE_CHOICE)) {
                    questionAnswerResponseList.add(QuestionAnswerResponse.builder()
                            .id(qa.getId())
                            .answer(qa.getAnswer())
                            .build());
                }
            }

            this.questions.add(QuestionResponse.builder()
                            .id(q.getId())
                            .title(q.getTitle())
                            .text(q.getText())
                            .questionType(q.getQuestionType())
                            .questionAnswers(questionAnswerResponseList)
                    .build());
        }
    }
}
