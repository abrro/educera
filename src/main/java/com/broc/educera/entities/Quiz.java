package com.broc.educera.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String rules;
    private LocalDateTime startTime;
    private LocalDateTime accessibleUntil;
    private Integer duration;
    private QuizStatus quizStatus;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "quiz")
    private List<QuizParticipation> quizParticipations;

    @OneToMany(mappedBy = "quiz")
    private List<ChoiceQuestion> choiceQuestions;

    @OneToMany(mappedBy = "quiz")
    private List<ShortAnswerQuestion> shortAnswerQuestions;
}
