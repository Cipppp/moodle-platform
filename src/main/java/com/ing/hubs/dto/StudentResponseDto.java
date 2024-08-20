package com.ing.hubs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponseDto {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
}
