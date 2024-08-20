package com.ing.hubs.service;

import com.ing.hubs.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PermissionServiceTest {
    private EnrollmentRequestService enrollmentRequestService;
    private CourseService courseService;
    private GradeService gradeService;
    private ScheduleService scheduleService;
    private PermissionService permissionService;
    Authentication authentication;

    @BeforeEach
    public void setup() {
        enrollmentRequestService = mock(EnrollmentRequestService.class);
        courseService = mock(CourseService.class);
        gradeService = mock(GradeService.class);
        scheduleService = mock(ScheduleService.class);
        permissionService = new PermissionService(
                enrollmentRequestService,
                courseService,
                gradeService,
                scheduleService
        );

        authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Nested
    class CanDeleteEnrollmentRequest {
        @Test
        void userIsOwner_ReturnsTrue() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            EnrollmentRequest enrollmentRequest =
                    EnrollmentRequest.builder()
                            .id(1L)
                            .student(student)
                            .course(new Course())
                            .status(EnrollmentRequestStatus.PENDING)
                            .build();

            setLoggedInUser(student);
            when(enrollmentRequestService.getEnrollmentRequestById(1L)).thenReturn(enrollmentRequest);

            assertTrue(permissionService.canDeleteEnrollmentRequest(1L));
            verify(enrollmentRequestService).getEnrollmentRequestById(1L);
        }

        @Test
        void userIsNotOwner_ReturnsFalse() {
            User firstStudent = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            User secondStudent = User.builder()
                    .id(2L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            EnrollmentRequest enrollmentRequest =
                    EnrollmentRequest.builder()
                            .id(1L)
                            .student(secondStudent)
                            .course(new Course())
                            .status(EnrollmentRequestStatus.PENDING)
                            .build();

            setLoggedInUser(firstStudent);
            when(enrollmentRequestService.getEnrollmentRequestById(1L)).thenReturn(enrollmentRequest);

            assertFalse(permissionService.canDeleteEnrollmentRequest(1L));
            verify(enrollmentRequestService).getEnrollmentRequestById(1L);
        }
    }

    @Nested
    class CanUpdateEnrollmentRequest {
        @Test
        void userIsOwner_ReturnsTrue() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            User teacher = User.builder()
                    .id(2L)
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            Course course = Course.builder()
                    .teacher(teacher)
                    .build();
            EnrollmentRequest enrollmentRequest =
                    EnrollmentRequest.builder()
                            .id(1L)
                            .student(student)
                            .course(course)
                            .status(EnrollmentRequestStatus.PENDING)
                            .build();

            setLoggedInUser(teacher);
            when(enrollmentRequestService.getEnrollmentRequestById(1L)).thenReturn(enrollmentRequest);

            assertTrue(permissionService.canUpdateEnrollmentRequest(1L));
            verify(enrollmentRequestService).getEnrollmentRequestById(1L);
        }

        @Test
        void userIsNotOwner_ReturnsFalse() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            User firstTeacher = User.builder()
                    .id(2L)
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            User secondTeacher = User.builder()
                    .id(3L)
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            Course course = Course.builder()
                    .teacher(firstTeacher)
                    .build();
            EnrollmentRequest enrollmentRequest =
                    EnrollmentRequest.builder()
                            .id(1L)
                            .student(student)
                            .course(course)
                            .status(EnrollmentRequestStatus.PENDING)
                            .build();

            setLoggedInUser(secondTeacher);
            when(enrollmentRequestService.getEnrollmentRequestById(1L)).thenReturn(enrollmentRequest);

            assertFalse(permissionService.canUpdateEnrollmentRequest(1L));
            verify(enrollmentRequestService).getEnrollmentRequestById(1L);
        }
    }

    @Nested
    class CourseBelongsToUser {
        @Test
        void userIsOwner_ReturnsTrue() {
            User teacher = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            Course course = Course.builder()
                    .id(2L)
                    .teacher(teacher)
                    .build();

            setLoggedInUser(teacher);
            when(courseService.getCourseById(1L)).thenReturn(course);

            assertTrue(permissionService.courseBelongsToUser(1L));
            verify(courseService).getCourseById(1L);
        }

        @Test
        void userIsNotOwner_ReturnsFalse() {
            User firstTeacher = User.builder()
                    .id(2L)
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            User secondTeacher = User.builder()
                    .id(3L)
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            Course course = Course.builder()
                    .id(1L)
                    .teacher(firstTeacher)
                    .build();

            setLoggedInUser(secondTeacher);
            when(courseService.getCourseById(1L)).thenReturn(course);

            assertFalse(permissionService.courseBelongsToUser(1L));
            verify(courseService).getCourseById(1L);
        }
    }

    @Nested
    class CanUpdateGrade {
        @Test
        void userIsCourseOwner_ReturnsTrue() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            User teacher = User.builder()
                    .id(2L)
                    .username("teacher")
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            Course course = Course.builder()
                    .id(3L)
                    .teacher(teacher)
                    .build();
            Grade grade = Grade.builder()
                    .id(4L)
                    .course(course)
                    .student(student)
                    .build();

            setLoggedInUser(teacher);

            when(gradeService.getGradeById(4L)).thenReturn(grade);

            assertTrue(permissionService.canUpdateGrade(4L));
            verify(gradeService).getGradeById(4L);
        }

        @Test
        void userIsNotCourseOwner_ReturnsFalse() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            User firstTeacher = User.builder()
                    .id(2L)
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            User secondTeacher = User.builder()
                    .id(3L)
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            Course course = Course.builder()
                    .id(4L)
                    .teacher(firstTeacher)
                    .build();
            Grade grade = Grade.builder()
                    .id(5L)
                    .course(course)
                    .student(student)
                    .build();

            setLoggedInUser(secondTeacher);
            when(gradeService.getGradeById(5L)).thenReturn(grade);

            assertFalse(permissionService.canUpdateGrade(5L));
            verify(gradeService).getGradeById(5L);
        }

        @Test
        void userIsNotOwnerAndIsAnotherStudent_ReturnsFalse() {
            User teacher = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            User firstStudent = User.builder()
                    .id(2L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            User secondStudent = User.builder()
                    .id(3L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            Course course = Course.builder()
                    .id(4L)
                    .teacher(teacher)
                    .build();
            Grade grade = Grade.builder()
                    .id(5L)
                    .course(course)
                    .student(firstStudent)
                    .build();

            setLoggedInUser(secondStudent);
            when(gradeService.getGradeById(5L)).thenReturn(grade);

            assertFalse(permissionService.canUpdateGrade(5L));
            verify(gradeService).getGradeById(5L);
        }

    }

    @Nested
    class CanViewGrade {
        @Test
        void userIsCourseOwner_ReturnsTrue() {
            User teacher = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            Course course = Course.builder()
                    .id(2L)
                    .teacher(teacher)
                    .build();
            Grade grade = Grade.builder()
                    .id(3L)
                    .course(course)
                    .build();

            setLoggedInUser(teacher);
            when(gradeService.getGradeById(3L)).thenReturn(grade);

            assertTrue(permissionService.canViewGrade(3L));
            verify(gradeService).getGradeById(3L);
        }

        @Test
        void userIsGradeOwner_ReturnsTrue() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            User teacher = User.builder()
                    .id(2L)
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            Course course = Course.builder()
                    .id(3L)
                    .teacher(teacher)
                    .build();
            Grade grade = Grade.builder()
                    .id(4L)
                    .course(course)
                    .student(student)
                    .build();

            setLoggedInUser(student);
            when(gradeService.getGradeById(4L)).thenReturn(grade);

            assertTrue(permissionService.canViewGrade(4L));
            verify(gradeService).getGradeById(4L);
        }

        @Test
        void userIsNotCourseOwnerAndIsTeacher_ReturnsFalse() {
            User firstTeacher = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            User secondTeacher = User.builder()
                    .id(2L)
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            Course course = Course.builder()
                    .id(3L)
                    .teacher(firstTeacher)
                    .build();
            Grade grade = Grade.builder()
                    .id(4L)
                    .course(course)
                    .build();

            setLoggedInUser(secondTeacher);
            when(gradeService.getGradeById(4L)).thenReturn(grade);

            assertFalse(permissionService.canViewGrade(4L));
            verify(gradeService).getGradeById(4L);
        }

        @Test
        void userIsNotGradeOwnerAndIsStudent_ReturnsFalse() {
            User firstStudent = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            User secondStudent = User.builder()
                    .id(2L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            Course course = Course.builder()
                    .id(3L)
                    .build();
            Grade grade = Grade.builder()
                    .id(4L)
                    .course(course)
                    .student(firstStudent)
                    .build();

            setLoggedInUser(secondStudent);
            when(gradeService.getGradeById(4L)).thenReturn(grade);

            assertFalse(permissionService.canViewGrade(4L));
            verify(gradeService).getGradeById(4L);
        }

        @Test
        void userIsNotStudentOrTeacher_ReturnsFalse() {
            UserType notAValidEnum = mock(UserType.class);
            User user = User.builder()
                    .roles(Set.of(notAValidEnum))
                    .id(1L)
                    .build();
            User student = User.builder()
                    .id(2L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            User teacher = User.builder()
                    .id(3L)
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            Course course = Course.builder()
                    .teacher(teacher)
                    .id(4L)
                    .build();
            Grade grade = Grade.builder()
                    .id(5L)
                    .course(course)
                    .student(student)
                    .build();

            setLoggedInUser(user);
            when(gradeService.getGradeById(5L)).thenReturn(grade);



            assertFalse(permissionService.canViewGrade(5L));
            verify(gradeService).getGradeById(5L);
        }

    }

    @Nested
    class CanUpdateSchedule {
        @Test
        void userIsCourseOwner_ReturnsTrue() {
            User teacher = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            Course course = Course.builder()
                    .id(2L)
                    .teacher(teacher)
                    .build();
            Schedule schedule = Schedule.builder()
                    .id(3L)
                    .course(course)
                    .build();

            setLoggedInUser(teacher);
            when(scheduleService.getScheduleById(3L)).thenReturn(schedule);

            assertTrue(permissionService.canUpdateSchedule(3L));
            verify(scheduleService).getScheduleById(3L);
        }

        @Test
        void userIsNotCourseOwner_ReturnsFalse() {
            User firstTeacher = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            User secondTeacher = User.builder()
                    .id(2L)
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            Course course = Course.builder()
                    .id(3L)
                    .teacher(firstTeacher)
                    .build();
            Schedule schedule = Schedule.builder()
                    .id(4L)
                    .course(course)
                    .build();

            setLoggedInUser(secondTeacher);
            when(scheduleService.getScheduleById(4L)).thenReturn(schedule);

            assertFalse(permissionService.canUpdateSchedule(4L));
            verify(scheduleService).getScheduleById(4L);
        }
    }

    private void setLoggedInUser(User user) {
        when(authentication.getPrincipal()).thenReturn(user);
    }
}
