package com.ing.hubs.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@NoArgsConstructor
@Table(name = "schedule")
@Data
@Builder
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "start_date")
    @Builder.Default
    private LocalDate startDate = LocalDate.now();

    @Column(nullable = false, name = "end_date")
    @Builder.Default
    private LocalDate endDate = LocalDate.now();

    @Column(nullable = false, name = "week_day")
    @Enumerated(value = EnumType.ORDINAL)
    private WeekDay weekDay = WeekDay.MON;

    @Column(nullable = false, name = "start_time")
    @Builder.Default
    private LocalTime startTime = LocalTime.now();

    @Column(nullable = false, name = "end_time")
    @Builder.Default
    private LocalTime endTime = LocalTime.now();

    @ManyToOne
    @JoinColumn(name="course_id", nullable=false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Course course;
}
