

package com.ing.hubs.service;

import com.ing.hubs.dto.GradeDto;
import com.ing.hubs.entity.Course;
import com.ing.hubs.entity.Grade;
import com.ing.hubs.entity.User;
import com.ing.hubs.entity.UserType;
import com.ing.hubs.exception.StudentAlreadyGradedException;
import com.ing.hubs.exception.StudentNotEnrolledException;
import com.ing.hubs.mapper.Mapper;
import com.ing.hubs.repository.GradeRepository;
import com.ing.hubs.repository.StudentCourseRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



public class GradeServiceTest {
    private GradeRepository gradeRepository;

    private StudentCourseRepository studentCourseRepository;

    private CourseService courseService;

    private UserService userService;

    private GradeService gradeService;

    private User student;
    private Course course;
    private Grade grade;
    private GradeDto gradeDto;

    Authentication authentication;

    @BeforeEach
    public void setup() {
        gradeRepository = mock(GradeRepository.class);
        courseService = mock(CourseService.class);
        gradeService = mock(GradeService.class);
        studentCourseRepository = mock(StudentCourseRepository.class);
        userService = mock(UserService.class);
        Mapper mapper = new Mapper(new ModelMapper());

        gradeService = new GradeService(
                gradeRepository,
                studentCourseRepository,
                courseService,
                userService,
                mapper
        );

        course = Course.builder()
                .id(1L)
                .name("OOP")
                .description("Something")
                .maxAttendees(30)
                .teacher(User.builder()
                        .id(1L)
                        .username("Duca")
                        .fullName("Duca Iulian")
                        .email("duca@gmail.com")
                        .phoneNumber("0774657614")
                        .password("duca12345")
                        .roles(Set.of(UserType.TEACHER))
                        .build())
                .schedule(new ArrayList<>())
                .build();

        student = User.builder()
                .id(1L)
                .username("Spike")
                .fullName("Spike cat")
                .email("spike@gmail.com")
                .phoneNumber("0774657614")
                .password("spike1234")
                .roles(Set.of(UserType.STUDENT))
                .build();

        grade = Grade.builder()
                .id(1L)
                .course(course)
                .student(student)
                .gradedAt(LocalDateTime.now())
                .score(90.f)
                .build();

        gradeDto = GradeDto.builder()
                .courseId(course.getId())
                .studentId(student.getId())
                .score(90.f)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


    @Nested
    class CreateGrade {
        @Test
        void shouldCreateGradeSuccessfully() {
            when(courseService.getCourseById(gradeDto.getCourseId())).thenReturn(course);
            when(userService.getUserById(gradeDto.getStudentId())).thenReturn(student);
            when(studentCourseRepository.existsByCourseAndStudent(course, student)).thenReturn(true);
            when(gradeRepository.existsByCourseAndStudent(course, student)).thenReturn(false);
            when(gradeRepository.save(any(Grade.class))).thenReturn(grade);

            assertNotNull(gradeService.createGrade(gradeDto));
            verify(gradeRepository).save(any(Grade.class));
        }

        @Test
        void shouldThrowStudentNotEnrolledException() {
            when(courseService.getCourseById(gradeDto.getCourseId())).thenReturn(course);
            when(userService.getUserById(gradeDto.getStudentId())).thenReturn(student);
            when(studentCourseRepository.existsByCourseAndStudent(course, student)).thenReturn(false);

            assertThrows(StudentNotEnrolledException.class, () -> gradeService.createGrade(gradeDto));
        }

        @Test
        void shouldThrowStudentAlreadyGradedException() {
            when(courseService.getCourseById(gradeDto.getCourseId())).thenReturn(course);
            when(userService.getUserById(gradeDto.getStudentId())).thenReturn(student);
            when(studentCourseRepository.existsByCourseAndStudent(course, student)).thenReturn(true);
            when(gradeRepository.existsByCourseAndStudent(course, student)).thenReturn(true);

            assertThrows(StudentAlreadyGradedException.class, () -> gradeService.createGrade(gradeDto));
        }
    }

    @Nested
    class FindGrades {
        @Test
        void shouldReturnAllGrades() {
            List<Grade> grades = List.of(grade);
            when(gradeRepository.findAll()).thenReturn(grades);

            List<Grade> result = gradeService.findAllGrades();

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(grades.size(), result.size());
        }
    }

    @Nested
    class UpdateGrade {
        @Test
        void shouldUpdateGradeSuccessfully() {
            when(gradeRepository.findById(anyLong())).thenReturn(Optional.of(grade));
            when(gradeRepository.save(any(Grade.class))).thenReturn(grade);

            GradeDto result = gradeService.updateGrade(gradeDto.getScore(), grade.getId());

            assertNotNull(result);
            assertEquals(gradeDto.getScore(), result.getScore());
            verify(gradeRepository).save(any(Grade.class));
        }
    }

    @Nested
    class GetGradeById {
        @Test
        void shouldReturnGrade() {
            when(gradeRepository.findById(anyLong())).thenReturn(Optional.of(grade));

            Grade result = gradeService.getGradeById(grade.getId());

            assertNotNull(result);
            assertEquals(grade.getId(), result.getId());
        }
    }

    @Nested
    class GetGradeDtoById {
        @Test
        void shouldReturnGradesForCurrentUser() {
            List<Grade> grades = List.of(grade);

            authentication = mock(Authentication.class);
            setLoggedInUser(student);

            when(gradeRepository.findAllByStudentId(student.getId())).thenReturn(grades);

            List<GradeDto> result = gradeService.findGradesForCurrentUser();

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(grades.size(), result.size());
            assertEquals(gradeDto.getScore(), result.get(0).getScore());

            verify(gradeRepository).findAllByStudentId(student.getId());
        }

        @Test
        void shouldReturnGradeDtoById() {
            when(gradeRepository.findById(grade.getId())).thenReturn(Optional.of(grade));

            GradeDto result = gradeService.getGradeDtoById(grade.getId());

            assertNotNull(result);
            assertEquals(gradeDto.getCourseId(), result.getCourseId());
            assertEquals(gradeDto.getStudentId(), result.getStudentId());
            assertEquals(gradeDto.getScore(), result.getScore());

            verify(gradeRepository).findById(grade.getId());
        }
    }

    @Nested
    class FindGradesByCourseId {
        @Test
        void shouldReturnGradesByCourseId() {
            List<Grade> grades = List.of(grade);

            when(gradeRepository.findAllByCourseId(course.getId())).thenReturn(grades);

            List<GradeDto> result = gradeService.findGradesByCourseId(course.getId(), null, null);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(grades.size(), result.size());
            assertEquals(gradeDto.getScore(), result.get(0).getScore());
            assertEquals(gradeDto.getStudentId(), result.get(0).getStudentId());

            verify(gradeRepository).findAllByCourseId(course.getId());
        }
    }


    @Nested
    class FindGradesForCurrentUser {
        @Test
        void shouldReturnGradesForCurrentUser() {
            List<Grade> grades = List.of(grade);

            authentication = mock(Authentication.class);
            setLoggedInUser(student);

            when(gradeRepository.findAllByStudentId(student.getId())).thenReturn(grades);

            List<GradeDto> result = gradeService.findGradesForCurrentUser();

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(grades.size(), result.size());
            assertEquals(gradeDto.getScore(), result.get(0).getScore());
            assertEquals(gradeDto.getStudentId(), result.get(0).getStudentId());

            verify(gradeRepository).findAllByStudentId(student.getId());
        }
    }

    private void setLoggedInUser(User user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}


