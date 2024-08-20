package com.ing.hubs.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @Min(1)
    private Integer maxAttendees;
    @NotEmpty
    @Valid
    private List<ScheduleDto> schedules;
}
