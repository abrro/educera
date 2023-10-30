package com.broc.educera.dtos.requests;

import com.broc.educera.entities.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortAnswerQuestionRequest {
    private String title;
    private String text;
    private Double maximumPoints;
    private QuestionType questionType;
    private Boolean caseSensitivity;
}
