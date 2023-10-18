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
public class ShortAnswerWildcard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer percentageValue;
    private String answer;

    @ManyToOne
    @JoinColumn(name = "short_answer_question_id")
    private ShortAnswerQuestion shortAnswerQuestion;
}
