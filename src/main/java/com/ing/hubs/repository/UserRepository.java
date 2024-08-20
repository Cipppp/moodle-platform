package com.ing.hubs.repository;

import com.ing.hubs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query(value = """
            select *
            from user
            where id in (
                select student_id
                from student_course
                where course_id = ?1
            );""",
            nativeQuery = true)
    List<User> findStudentsByCourseId(Long courseId);

    @Query(value = """
            select *
            from user
            where id not in (
                select student_id
                from grade
                where course_id = ?1
            ) and id in (
                select student_id
                from student_course
                where course_id = ?1
            );
            """,
            nativeQuery = true)
    List<User> getUngradedStudentsForCourse(Long courseId);

    @Query(value = """
            select *
            from user
            where id in (
                select student_id
                from grade
                where course_id = ?1
            ) and id in (
                select student_id
                from student_course
                where course_id = ?1
            );
            """,
            nativeQuery = true)
    List<User> getGradedStudentsForCourse(Long courseId);

    boolean existsByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
