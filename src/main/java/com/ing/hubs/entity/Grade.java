package com.ing.hubs.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table(name = "grade")
@Data
@Builder
@AllArgsConstructor
public class Grade {
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

    @Column(nullable = false, name = "graded_at")
    @Builder.Default
    private LocalDateTime gradedAt = LocalDateTime.now();

    @Column
    private Float score;
}
