package com.ing.hubs.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseUpdateDto {
    private String name;
    private String description;
    @Min(1)
    private Integer maxAttendees;
}
