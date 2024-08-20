package com.ing.hubs.service;

import com.ing.hubs.entity.EnrollmentRequest;
import com.ing.hubs.entity.StudentCourse;
import com.ing.hubs.repository.StudentCourseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StudentCourseService {
    private StudentCourseRepository studentCourseRepository;

    public void enrollStudent(EnrollmentRequest enrollmentRequest) {
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setCourse(enrollmentRequest.getCourse());
        studentCourse.setStudent(enrollmentRequest.getStudent());

        studentCourseRepository.save(studentCourse);
    }
}
