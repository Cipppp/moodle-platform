package com.ing.hubs.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Table(name = "student_course")
@Data
@Builder
@AllArgsConstructor
public class StudentCourse {
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
}
