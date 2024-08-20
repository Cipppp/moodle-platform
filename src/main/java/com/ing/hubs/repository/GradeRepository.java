package com.ing.hubs.repository;

import com.ing.hubs.entity.Course;
import com.ing.hubs.entity.Grade;
import com.ing.hubs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByCourseId(Long courseId);

    @Query(value = """
            select *
            from grade
            where course_id = ?1
            """,
            nativeQuery = true)
    List<Grade> findAllByCourseId(Long courseId);

    @Query(value = """
            select *
            from grade
            where student_id = ?1
            """,
            nativeQuery = true)
    List<Grade> findAllByStudentId(Long studentId);

    boolean existsByCourseAndStudent(Course course, User student);

}
