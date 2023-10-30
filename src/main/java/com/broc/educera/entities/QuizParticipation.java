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
public class QuizParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private QuizParticipationState quizParticipationState;
    private LocalDateTime accessTime;
    private LocalDateTime submissionTime;

    private Double totalPoints;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @OneToMany(mappedBy = "quizParticipation", cascade = CascadeType.ALL)
    private List<QuestionResponse> questionResponses;

}
