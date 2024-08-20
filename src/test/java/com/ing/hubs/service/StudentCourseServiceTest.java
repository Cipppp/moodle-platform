package com.ing.hubs.service;

import com.ing.hubs.entity.Course;
import com.ing.hubs.entity.EnrollmentRequest;
import com.ing.hubs.entity.StudentCourse;
import com.ing.hubs.entity.User;
import com.ing.hubs.repository.StudentCourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StudentCourseServiceTest {

    private StudentCourseRepository studentCourseRepository;

    private StudentCourseService studentCourseService;

    @BeforeEach
    public void setup() {
        studentCourseRepository = mock(StudentCourseRepository.class);
        studentCourseService = new StudentCourseService(studentCourseRepository);
    }

    @Nested
    class EnrollStudent {
        @Test
        void successfulEnrollmentVerify() {
            EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
            Course course = new Course();
            User student = new User();
            enrollmentRequest.setCourse(course);
            enrollmentRequest.setStudent(student);

            when(studentCourseRepository.save(any(StudentCourse.class))).thenReturn(new StudentCourse());

            studentCourseService.enrollStudent(enrollmentRequest);

            verify(studentCourseRepository).save(argThat(studentCourse ->
                    studentCourse.getCourse().equals(enrollmentRequest.getCourse()) &&
                            studentCourse.getStudent().equals(enrollmentRequest.getStudent())
            ));

        }

        @Test
        void successfulEnrollmentAssetDoesNotThrow() {
            EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
            Course course = new Course();
            User student = new User();
            enrollmentRequest.setCourse(course);
            enrollmentRequest.setStudent(student);

            studentCourseService.enrollStudent(enrollmentRequest);

            assertDoesNotThrow(() -> studentCourseRepository.save(any()));
        }

    }

}
