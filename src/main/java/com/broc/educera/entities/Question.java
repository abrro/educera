package com.broc.educera.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String text;

    private Integer ordinalNum;

    private boolean caseSensitive;

    @Min(0)
    private Double maximumPoints;

    private QuestionType questionType;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionAnswer> questionAnswers;

    @JsonIgnore
    @OneToMany(mappedBy = "question")
    private List<QuestionResponse> questionResponses;

    public void addAnswer(QuestionAnswer questionAnswer) {
        this.questionAnswers.add(questionAnswer);
    }

    public void removeAnswer(QuestionAnswer questionAnswer) {
        this.questionAnswers.remove(questionAnswer);
    }

    public void removeAllAnswers(){
        this.questionAnswers.clear();;
    }

    public void setAnswers(List<QuestionAnswer> answers) {
        this.questionAnswers.clear();
        if (answers != null) {
            this.questionAnswers.addAll(answers);
        }
    }

}
