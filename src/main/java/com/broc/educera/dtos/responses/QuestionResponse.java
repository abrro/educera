package com.broc.educera.dtos.responses;

import com.broc.educera.entities.QuestionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class QuestionResponse {
    private Long id;

    private String title;

    private String text;

    private Double maximumPoints;

    private QuestionType questionType;

    List<QuestionAnswerResponse> questionAnswers;

}
