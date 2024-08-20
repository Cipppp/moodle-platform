package com.ing.hubs.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ing.hubs.entity.WeekDay;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDto {
    @NotBlank
    private String name;
    @NotNull
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate startDate;
    @NotNull
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate endDate;
    @NotNull
    private WeekDay weekDay;
    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;
}
