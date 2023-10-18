package com.broc.educera.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortAnswerQuestionResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String shortAnswerResponse;

    @ManyToOne
    @JoinColumn(name = "short_answer_question_id")
    private ShortAnswerQuestion shortAnswerQuestion;

    @ManyToOne
    @JoinColumn(name = "quiz_participation_id")
    private QuizParticipation quizParticipation;
}
