package com.ing.hubs.resource;

import com.ing.hubs.dto.CourseDto;
import com.ing.hubs.dto.CourseResponseDto;
import com.ing.hubs.dto.CourseUpdateDto;
import com.ing.hubs.service.CourseService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/courses")
@Secured("ROLE_TEACHER")
public class TeacherCourseResource {
    private CourseService courseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponseDto addCourse(@RequestBody @Valid CourseDto courseDto) {
        return courseService.createCourse(courseDto);
    }

    @PutMapping("/{courseId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("@permissionService.courseBelongsToUser(#courseId)")
    public CourseResponseDto updateCourse(@PathVariable Long courseId, @RequestBody @Valid CourseUpdateDto courseUpdateDto) {
        return courseService.updateCourse(courseId, courseUpdateDto);
    }

    @GetMapping("/created")
    public List<CourseResponseDto> getAllCreatedCourses(@RequestParam(value = "name", required = false) String courseName) {
        return courseService.getAllCreatedCourses(courseName);
    }

}
