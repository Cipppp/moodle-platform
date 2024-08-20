package com.ing.hubs.repository;

import com.ing.hubs.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTeacherId(Long teacherId);

    List<Course> findByTeacherIdAndNameContaining(Long teacherId, String name);

    @Query(value = """
            select *
            from course
            where id in (
                select course_id
                from student_course
                where student_id = ?1
            );""",
            nativeQuery = true)
    List<Course> findCoursesByStudentId(Long studentId);
}
