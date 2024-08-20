package com.ing.hubs.repository;

import com.ing.hubs.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByCourseId(Long courseId);

    @Query(value = """
            select *
            from schedule
            where course_id in (
                select course_id
                from student_course
                where student_id = ?1
            );
            """,
            nativeQuery = true)
    List<Schedule> findByStudentId(Long courseId);

    @Query(value= """
            select s 
            from Schedule s 
            join s.course c 
            where c.teacher.id = :teacherId
            """)
    List<Schedule> findByTeacherId(Long teacherId);

}
