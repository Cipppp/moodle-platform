package com.ing.hubs.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Table(name = "enrollment_request")
@Data
@Builder
@AllArgsConstructor
public class EnrollmentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="course_id", nullable=false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Course course;

    @ManyToOne
    @JoinColumn(name="student_id", nullable=false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User student;

    @Column(nullable = false, name = "status")
    @Enumerated(value = EnumType.ORDINAL)
    private EnrollmentRequestStatus status = EnrollmentRequestStatus.PENDING;
}
