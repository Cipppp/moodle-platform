package com.ing.hubs.dto;

import com.ing.hubs.entity.EnrollmentRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentRequestUpdateDto {
    private EnrollmentRequestStatus status;
}
