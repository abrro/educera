package com.broc.educera.repositories;

import com.broc.educera.entities.CourseEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    Page<CourseEnrollment> findAllByCourse_Id(Long course_id, Pageable pageable);
    Optional<CourseEnrollment> findCourseEnrollmentByStudent_IdAndCourse_Id(Long student_id, Long course_id);
}
