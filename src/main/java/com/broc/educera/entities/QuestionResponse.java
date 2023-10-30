package com.broc.educera.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class QuestionResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String response;
    private Double acquiredPoints;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "quiz_part_id")
    private QuizParticipation quizParticipation;
}
