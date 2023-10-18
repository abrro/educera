package com.broc.educera.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChoiceQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private Integer maximumPoints;
    private ChoiceQuestionType choiceQuestionType;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @OneToMany(mappedBy = "choiceQuestion")
    private List<ChoiceQuestionResponse> choiceQuestionResponses;

    @OneToMany(mappedBy = "choiceQuestion")
    private List<ChoiceQuestionAnswer> choiceQuestionAnswers;

}
