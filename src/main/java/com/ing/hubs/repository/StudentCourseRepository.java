package com.ing.hubs.repository;

import com.ing.hubs.entity.Course;
import com.ing.hubs.entity.StudentCourse;
import com.ing.hubs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {
    boolean existsByCourseAndStudent(Course course, User student);
    Integer countByCourse(Course course);
}
