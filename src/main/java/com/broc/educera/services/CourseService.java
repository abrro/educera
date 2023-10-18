package com.broc.educera.services;

import com.broc.educera.entities.Course;
import com.broc.educera.entities.CourseEnrollment;
import com.broc.educera.repositories.CourseEnrollmentRepository;
import com.broc.educera.repositories.CourseRepository;
import com.broc.educera.repositories.common.CommonDaoInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService implements CommonDaoInterface<Course, Long> {

    private final CourseRepository courseRepository;

    public Page<Course> findAll(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    @Override
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    public <S extends Course> S save(S course) {
        return courseRepository.save(course);
    }

    @Override
    public void deleteById(Long id) {
        courseRepository.deleteById(id);
    }

}
