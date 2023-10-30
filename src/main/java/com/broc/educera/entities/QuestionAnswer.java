package com.broc.educera.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
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
public class QuestionAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.StudentQuestionsView.class)
    private Long id;

    //field which content is treated differently depending on type of question
    @JsonView(Views.StudentQuestionsView.class)
    private String answer;

    private Double percentageValue;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
}
