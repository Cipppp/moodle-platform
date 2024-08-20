package com.ing.hubs.service;


import com.ing.hubs.dto.GradeDto;
import com.ing.hubs.entity.Course;
import com.ing.hubs.entity.Grade;
import com.ing.hubs.entity.User;
import com.ing.hubs.exception.GradeNotFoundException;
import com.ing.hubs.exception.StudentAlreadyGradedException;
import com.ing.hubs.exception.StudentNotEnrolledException;
import com.ing.hubs.mapper.Mapper;
import com.ing.hubs.repository.GradeRepository;
import com.ing.hubs.repository.StudentCourseRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GradeService {
    private GradeRepository gradeRepository;
    private StudentCourseRepository studentCourseRepository;
    private CourseService courseService;
    private UserService userService;
    private Mapper mapper;

    public GradeDto createGrade(GradeDto gradeDto) {
        Course course = courseService.getCourseById(gradeDto.getCourseId());
        User student = userService.getUserById(gradeDto.getStudentId());

        validateStudentEnrollment(course, student);
        validateStudentAlreadyGraded(course, student);

        Grade grade = Grade.builder()
                .student(student)
                .course(course)
                .score(gradeDto.getScore())
                .build();
        return mapper.toDto(gradeRepository.save(grade));
    }

    public List<Grade> findAllGrades() {
        return gradeRepository.findAll();
    }

    public GradeDto updateGrade(Float score, Long gradeId) {
        Grade grade = getGradeById(gradeId);
        grade.setScore(score);
        return mapper.toDto(gradeRepository.save(grade));
    }

    public Grade getGradeById(Long gradeId) {
        return gradeRepository.findById(gradeId)
                .orElseThrow(GradeNotFoundException::new);
    }

    public GradeDto getGradeDtoById(Long gradeId) {
        return mapper.toDto(getGradeById(gradeId));
    }

    public List<GradeDto> findGradesByCourseId(Long courseId, Float lowerBound, Float upperBound) {
        return gradeRepository.findAllByCourseId(courseId).stream()
                .filter(grade -> Optional.ofNullable(lowerBound).map(threshold -> grade.getScore() > threshold).orElse(true))
                .filter(grade -> Optional.ofNullable(upperBound).map(threshold -> grade.getScore() < threshold).orElse(true))
                .map(grade -> mapper.toDto(grade))
                .toList();
    }

    public List<GradeDto> findGradesForCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return gradeRepository.findAllByStudentId(user.getId())
                .stream()
                .map(grade -> mapper.toDto(grade))
                .toList();
    }

    private void validateStudentAlreadyGraded(Course course, User student) {
        if (gradeRepository.existsByCourseAndStudent(course, student)) {
            throw new StudentAlreadyGradedException();
        }
    }

    private void validateStudentEnrollment(Course course, User student) {
        if (!studentCourseRepository.existsByCourseAndStudent(course, student)) {
            throw new StudentNotEnrolledException();
        }
    }
}
