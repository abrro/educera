package com.broc.educera.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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

    @NotBlank
    private String name;
    @NotBlank
    private String rules;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Europe/Belgarde")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime accessibleUntil;
    @NotNull
    @Min(1)
    private Integer duration;

    private Double maxPoints;

    private QuizStatus quizStatus;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @JsonIgnore
    @OneToMany(mappedBy = "quiz")
    private List<QuizParticipation> quizParticipations;

    @JsonIgnore
    @OneToMany(mappedBy = "quiz")
    private List<Question> questions;

}
