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
public class ShortAnswerQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String text;
    private Integer maximumPoints;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @OneToMany(mappedBy = "shortAnswerQuestion")
    private List<ShortAnswerWildcard> acceptedWildcards;

    @OneToMany(mappedBy = "shortAnswerQuestion")
    private List<ShortAnswerQuestionResponse> shortAnswerQuestionResponses;
}
