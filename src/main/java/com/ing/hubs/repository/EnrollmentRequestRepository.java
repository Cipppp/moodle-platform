package com.ing.hubs.repository;

import com.ing.hubs.entity.EnrollmentRequest;
import com.ing.hubs.entity.EnrollmentRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRequestRepository extends JpaRepository<EnrollmentRequest, Long> {
    List<EnrollmentRequest> findByCourseId(Long courseId);

    List<EnrollmentRequest> findByStudentId(Long studentId);

    @Query(value = """
            select *
            from enrollment_request
            where course_id = ?1
            """,
            nativeQuery = true)
    List<EnrollmentRequest> findPendingByCourseId(Long courseId);

    @Query(value = """
            select *
            from enrollment_request
            where student_id = ?1
            and status != 2
            """,
            nativeQuery = true)
    List<EnrollmentRequest> findPendingByStudentId(Long studentId);

    List<EnrollmentRequest> findByCourseIdAndStatus(Long courseId, EnrollmentRequestStatus status);

    List<EnrollmentRequest> findByStudentIdAndStatus(Long studentId, EnrollmentRequestStatus status);

    Optional<Object> findByStudentIdAndCourseId(Long id, Long courseId);

    boolean existsByCourseId(Long courseId);
}
