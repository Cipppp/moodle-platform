package com.ing.hubs.resource;

import com.ing.hubs.dto.CourseResponseDto;
import com.ing.hubs.dto.StudentResponseDto;
import com.ing.hubs.service.CourseService;
import com.ing.hubs.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@AllArgsConstructor
public class CourseResource {
    private CourseService courseService;
    private StudentService studentService;

    @GetMapping
    public List<CourseResponseDto> getCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{courseId}/students")
    public List<StudentResponseDto> getEnrolledStudents(@PathVariable Long courseId, @RequestParam(value = "graded", required = false) Boolean graded) {
        return studentService.getStudentsForCourse(courseId, graded);
    }

    @GetMapping("/{courseId}")
    public CourseResponseDto getCourseDetails(@PathVariable Long courseId) {
        return courseService.getCourseResponseDtoById(courseId);
    }

}
