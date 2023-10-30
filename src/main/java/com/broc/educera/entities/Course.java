package com.broc.educera.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
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
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    private String name;
    @NotEmpty
    private String description;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @JsonIgnore
    @OneToMany(mappedBy = "course")
    private List<CourseEnrollment> courseEnrollments;

    @JsonIgnore
    @OneToMany(mappedBy = "course")
    private List<Quiz> quizzes;

}
