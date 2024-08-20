package com.ing.hubs.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GradeDto {
    @NotNull
    private Long courseId;
    @NotNull
    private Long studentId;
    @Min(1)
    @Max(10)
    private Float score;
}
