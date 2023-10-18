package com.broc.educera.controllers;

import com.broc.educera.dtos.CourseRequest;
import com.broc.educera.entities.Course;
import com.broc.educera.entities.CourseEnrollment;
import com.broc.educera.entities.User;
import com.broc.educera.entities.UserType;
import com.broc.educera.services.CourseEnrollmentService;
import com.broc.educera.services.CourseService;
import com.broc.educera.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;
    private final CourseEnrollmentService courseEnrollmentService;

    @GetMapping
    public ResponseEntity<?> all(@RequestParam("page") Integer pageNumber,
                                 @RequestParam("size") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.unsorted());
        return ResponseEntity.ok().body(courseService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable Long id) {
        Course course = courseService.findById(id).orElseThrow();
        return ResponseEntity.ok().body(course);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@Valid @RequestBody CourseRequest courseRequest, @AuthenticationPrincipal User currentUser) {
        if(currentUser.getUserType().equals(UserType.ADMIN)) {
            User teacher = userService.findById(courseRequest.getTeacher_id()).orElseThrow();
            Course newCourse = Course.builder()
                    .name(courseRequest.getName())
                    .description(courseRequest.getDescription())
                    .teacher(teacher).build();
            newCourse = courseService.save(newCourse);
            return ResponseEntity.ok().body(newCourse);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> modify(@RequestBody @Valid CourseRequest courseRequest,
                                    @PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        if(currentUser.getUserType().equals(UserType.ADMIN)) {
            Course course = courseService.findById(id).orElseThrow();
            User teacher = userService.findById(courseRequest.getTeacher_id()).orElseThrow();
            course.setName(courseRequest.getName());
            course.setDescription(courseRequest.getDescription());
            course.setTeacher(teacher);
            return ResponseEntity.ok().body(course);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        if(currentUser.getUserType().equals(UserType.ADMIN)) {
            Course course = courseService.findById(id).orElseThrow();
            courseService.deleteById(course.getId());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<?> allEnrolledStudents(@PathVariable Long id,
                                                 @RequestParam("page") Integer pageNumber,
                                                 @RequestParam("size") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.unsorted());
        return ResponseEntity.ok().body(courseEnrollmentService.enrolledStudents(id, pageable));
    }

    @PostMapping(value = "/{id}/enroll",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> enrollStudent(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        Course course = courseService.findById(id).orElseThrow();
        Optional<CourseEnrollment> enrollmentOptional = courseEnrollmentService.findByStudentIdAndCourseId(currentUser.getId(), id);
        if(enrollmentOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Already enrolled on this course");
        } else {
            CourseEnrollment newCourseEnrollment = CourseEnrollment.builder()
                    .enrollmentDate(LocalDateTime.now())
                    .course(course)
                    .student(currentUser)
                    .build();
            return ResponseEntity.ok().body(courseEnrollmentService.save(newCourseEnrollment));
        }
    }

    @DeleteMapping("/unlist/{id}")
    public ResponseEntity<?> unlistStudent(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        //pogledaj ponovo kako bi ovo trebalo? da li trebaju provere da je student? mislim da ne treba jer auth princ
        Optional<CourseEnrollment> enrollmentOptional = courseEnrollmentService.findByStudentIdAndCourseId(currentUser.getId(), id);
        if (enrollmentOptional.isPresent()) {
            courseEnrollmentService.deleteById(enrollmentOptional.get().getId());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Not enrolled on course.");
        }
    }

}
