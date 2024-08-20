package com.ing.hubs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseResponseDto {
    private Long id;
    private String name;
    private String description;
    private Integer maxAttendees;
    private List<ScheduleResponseDto> schedule;
}
