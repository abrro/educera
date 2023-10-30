package com.broc.educera.seeders;


import com.broc.educera.entities.*;
import com.broc.educera.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cglib.core.Local;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserService userService;
    private final CourseService courseService;
    private final CourseEnrollmentService courseEnrollmentService;
    private final QuizService quizService;
    private final QuestionService questionService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        User admin_1 = User.builder()
                .email("john@gmail.com")
                .firstname("John")
                .lastname("Smith")
                .password(passwordEncoder.encode("admin"))
                .userType(UserType.ADMIN)
                .build();
        userService.save(admin_1);

        User teacher_1 = User.builder()
                .email("samuel@gmail.com")
                .firstname("Samuel")
                .lastname("Portman")
                .password(passwordEncoder.encode("sam123"))
                .userType(UserType.TEACHER)
                .build();
        userService.save(teacher_1);

        Course course_1 = Course.builder()
                .name("Java programming")
                .description("Basics of java programming language.")
                .teacher(teacher_1)
                .build();
        courseService.save(course_1);

        User student_1 = User.builder()
                .email("matthew@gmail.com")
                .firstname("Matthew")
                .lastname("Trainor")
                .password(passwordEncoder.encode("matt123"))
                .userType(UserType.STUDENT)
                .build();
        userService.save(student_1);

        User student_2 = User.builder()
                .email("laura@gmail.com")
                .firstname("Laura")
                .lastname("Moore")
                .password(passwordEncoder.encode("laura123"))
                .userType(UserType.STUDENT)
                .build();
        userService.save(student_2);

        CourseEnrollment ce_1 = CourseEnrollment.builder()
                .student(student_1)
                .course(course_1)
                .enrollmentDate(LocalDateTime.now())
                .build();
        courseEnrollmentService.save(ce_1);

        Quiz quiz_1 = Quiz.builder()
                .name("Database example quiz")
                .rules("Quiz lasts for 60 minutes. You can change answers for any question during the duration of this quiz.")
                .duration(1)
                .startTime(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MINUTES))
                .accessibleUntil(LocalDateTime.now().plusMinutes(10).truncatedTo(ChronoUnit.MINUTES))
                .course(course_1)
                .quizStatus(QuizStatus.SCHEDULED)
                .maxPoints(0.0)
                .build();
        quiz_1 = quizService.save(quiz_1);

        ZonedDateTime start = quiz_1.getStartTime().atZone(ZoneId.of("Europe/Belgrade"));
        ZonedDateTime close = quiz_1.getAccessibleUntil().atZone(ZoneId.of("Europe/Belgrade"));

        System.out.println(start.toInstant());
        //quizService.scheduleQuiz(quiz_1, start.toInstant());
        quizService.scheduleOpenQuiz(quiz_1.getId(), start.toInstant());
        quizService.scheduleCloseQuiz(quiz_1.getId(), close.toInstant());

        Question q_1 = Question.builder()
                .title("Question 1: Types of monosaccharides")
                .text("Choose all the examples or types of monosaccharides")
                .maximumPoints(10.0)
                .ordinalNum(null)
                .questionType(QuestionType.MULTI_CHOICE)
                .quiz(quiz_1)
                .build();
        Question q_2 = Question.builder()
                .title("Question 2: Not an author of Declaration of Independence")
                .text("Who out of following people was not one of the authors of Declaration of Independence?")
                .maximumPoints(10.0)
                .ordinalNum(null)
                .questionType(QuestionType.SINGLE_CHOICE)
                .quiz(quiz_1)
                .build();
        Question q_3 = Question.builder()
                .title("Question 3: Secretary of UN")
                .text("Who was the secretary of United nations between 2007 and 2016?")
                .maximumPoints(5.0)
                .ordinalNum(null)
                .questionType(QuestionType.SHORT_ANSWER)
                .caseSensitive(true)
                .quiz(quiz_1)
                .build();

        QuestionAnswer q_a0 = QuestionAnswer.builder()
                .answer("Glucose")
                .percentageValue(0.5)
                .question(q_1)
                .build();

        QuestionAnswer q_a1 = QuestionAnswer.builder()
                .answer("Fructose")
                .percentageValue(0.5)
                .question(q_1)
                .build();

        QuestionAnswer q_a2 = QuestionAnswer.builder()
                .answer("Maltose")
                .percentageValue(-0.25)
                .question(q_1)
                .build();

        QuestionAnswer q_a3 = QuestionAnswer.builder()
                .answer("Lactose")
                .percentageValue(-0.25)
                .question(q_1)
                .build();
 //================
        QuestionAnswer q_a4 = QuestionAnswer.builder()
                .answer("Thomas Jefferson")
                .percentageValue(-1.0)
                .question(q_2)
                .build();

        QuestionAnswer q_a5 = QuestionAnswer.builder()
                .answer("Benjamin Franklin")
                .percentageValue(-0.5)
                .question(q_2)
                .build();

        QuestionAnswer q_a6 = QuestionAnswer.builder()
                .answer("George Washington")
                .percentageValue(1.0)
                .question(q_2)
                .build();
//===============
        QuestionAnswer q_a7 = QuestionAnswer.builder()
                .answer("Ban Ki-Moon")
                .percentageValue(1.0)
                .question(q_3)
                .build();

        QuestionAnswer q_a8 = QuestionAnswer.builder()
                .answer("Ki-Moon Ban")
                .percentageValue(1.0)
                .question(q_3)
                .build();

        QuestionAnswer q_a9 = QuestionAnswer.builder()
                .answer("Ki-Moon")
                .percentageValue(0.5)
                .question(q_3)
                .build();

        List<QuestionAnswer> answers_q_1 = new ArrayList<>();
        answers_q_1.add(q_a0);
        answers_q_1.add(q_a1);
        answers_q_1.add(q_a2);
        answers_q_1.add(q_a3);

        q_1.setQuestionAnswers(answers_q_1);
        questionService.save(q_1);

        List<QuestionAnswer> answers_q_2 = new ArrayList<>();
        answers_q_2.add(q_a4);
        answers_q_2.add(q_a5);
        answers_q_2.add(q_a6);

        q_2.setQuestionAnswers(answers_q_2);
        questionService.save(q_2);

        List<QuestionAnswer> answers_q_3 = new ArrayList<>();
        answers_q_3.add(q_a7);
        answers_q_3.add(q_a8);
        answers_q_3.add(q_a9);

        q_3.setQuestionAnswers(answers_q_3);
        questionService.save(q_3);

        quiz_1.setMaxPoints(quiz_1.getMaxPoints() + q_1.getMaximumPoints() + q_2.getMaximumPoints() + q_3.getMaximumPoints());
        quizService.save(quiz_1);


    }
}
