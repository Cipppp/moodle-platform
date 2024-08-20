package com.ing.hubs.resource;

import com.ing.hubs.dto.CourseResponseDto;
import com.ing.hubs.service.CourseService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/courses")
@AllArgsConstructor
@Secured("ROLE_STUDENT")
public class StudentCourseResource {
    private CourseService courseService;

    @GetMapping("/enrolled")
    public List<CourseResponseDto> getCoursesEnrolledTo() {
        return courseService.getCoursesEnrolledTo();
    }

}

