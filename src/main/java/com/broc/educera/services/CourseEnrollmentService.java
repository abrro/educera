package com.broc.educera.services;

import com.broc.educera.entities.CourseEnrollment;
import com.broc.educera.repositories.CourseEnrollmentRepository;
import com.broc.educera.repositories.common.CommonDaoInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseEnrollmentService implements CommonDaoInterface<CourseEnrollment, Long> {
    private final CourseEnrollmentRepository courseEnrollmentRepository;

    public Optional<CourseEnrollment> findByStudentIdAndCourseId(Long student_id, Long course_id) {
        return courseEnrollmentRepository.findCourseEnrollmentByStudent_IdAndCourse_Id(student_id, course_id);
    }

    public Page<CourseEnrollment> enrolledStudents(Long course_id, Pageable pageable) {
        return courseEnrollmentRepository.findAllByCourse_Id(course_id, pageable);
    }

    @Override
    public Optional<CourseEnrollment> findById(Long id) {
        return courseEnrollmentRepository.findById(id);
    }

    @Override
    public <S extends CourseEnrollment> S save(S courseEnrollment) {
        return courseEnrollmentRepository.save(courseEnrollment);
    }

    @Override
    public void deleteById(Long id) {
        courseEnrollmentRepository.deleteById(id);
    }
}
