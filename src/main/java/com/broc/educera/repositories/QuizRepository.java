package com.broc.educera.repositories;

import com.broc.educera.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findAllByCourse_Id(Long course_id);
}
