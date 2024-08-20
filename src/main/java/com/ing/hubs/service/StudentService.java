package com.ing.hubs.service;

import com.ing.hubs.dto.StudentResponseDto;
import com.ing.hubs.entity.User;
import com.ing.hubs.mapper.Mapper;
import com.ing.hubs.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Optional.ofNullable;

@Service
@AllArgsConstructor
public class StudentService {
    private UserRepository userRepository;
    private Mapper mapper;

    public List<StudentResponseDto> getStudentsForCourse(Long courseId, Boolean graded) {
        List<User> students = ofNullable(graded).map(g -> getStudentsFilteredByGradeExistence(courseId, graded))
                .orElse(userRepository.findStudentsByCourseId(courseId));

        return students
                .stream()
                .map(student -> mapper.map(student, StudentResponseDto.class))
                .toList();
    }

    private List<User> getStudentsFilteredByGradeExistence(Long courseId, Boolean graded) {
        if (graded) {
            return userRepository.getGradedStudentsForCourse(courseId);
        }

        return userRepository.getUngradedStudentsForCourse(courseId);
    }

}
