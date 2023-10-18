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
public class ChoiceQuestionResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //string of chosen answers, for example 1,3,5 - selected answers out of 5 questions
    //or string of ids of chosen answers? 5L,7L....
    private String chosenAnswers;
    private String resultingPoints;

    @ManyToOne
    @JoinColumn(name = "choice_question_id")
    private ChoiceQuestion choiceQuestion;

    @ManyToOne
    @JoinColumn(name = "quiz_participation_id")
    private QuizParticipation quizParticipation;
}
