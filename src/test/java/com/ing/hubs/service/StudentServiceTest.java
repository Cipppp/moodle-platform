package com.ing.hubs.service;

import com.ing.hubs.dto.StudentResponseDto;
import com.ing.hubs.entity.User;
import com.ing.hubs.mapper.Mapper;
import com.ing.hubs.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private StudentService studentService;

    @Nested
    class GetStudentsForCourse {
        @Test
        void whenGradedIsTrue_ReturnsGradedStudents() {
            Long courseId = 1L;
            List<User> mockedUsers = List.of(new User());
            List<StudentResponseDto> mockedStudentDtos = List.of(new StudentResponseDto());
            when(userRepository.getGradedStudentsForCourse(courseId)).thenReturn(mockedUsers);
            when(mapper.map(any(User.class), any())).thenReturn(mockedStudentDtos.get(0));

            List<StudentResponseDto> result = studentService.getStudentsForCourse(courseId, true);

            assertEquals(mockedStudentDtos, result);
        }

        @Test
        void whenGradedIsFalse_ReturnsUngradedStudents() {
            Long courseId = 1L;
            List<User> mockedUsers = List.of(new User());
            List<StudentResponseDto> mockedStudentDtos = List.of(new StudentResponseDto());
            when(userRepository.getUngradedStudentsForCourse(courseId)).thenReturn(mockedUsers);
            when(mapper.map(any(User.class), any())).thenReturn(mockedStudentDtos.get(0));

            List<StudentResponseDto> result = studentService.getStudentsForCourse(courseId, false);

            assertEquals(mockedStudentDtos, result);
        }
    }
}