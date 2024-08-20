package com.ing.hubs.dto;

import com.ing.hubs.entity.EnrollmentRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrollmentRequestResponseDto {
    private Long id;
    private Long courseId;
    private Long studentId;
    private EnrollmentRequestStatus status;
}
