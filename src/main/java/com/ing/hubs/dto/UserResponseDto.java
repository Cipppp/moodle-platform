package com.ing.hubs.dto;

import com.ing.hubs.entity.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Set<UserType> roles;
}
