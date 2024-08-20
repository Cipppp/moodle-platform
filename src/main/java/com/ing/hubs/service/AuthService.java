package com.ing.hubs.service;

import com.ing.hubs.dto.JwtDto;
import com.ing.hubs.dto.UserCredentialsDto;
import com.ing.hubs.dto.UserDto;
import com.ing.hubs.dto.UserResponseDto;
import com.ing.hubs.entity.User;
import com.ing.hubs.entity.UserType;
import com.ing.hubs.mapper.Mapper;
import com.ing.hubs.security.JwtProvider;
import com.ing.hubs.service.validator.UserValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class AuthService {
    private UserService userService;
    private JwtProvider jwtProvider;
    private AuthenticationManager authenticationManager;
    private UserValidator userValidator;
    private Mapper mapper;

    public UserResponseDto registerTeacher(UserDto userDto) {
        userValidator.validateUserRegistration(userDto);
        return mapper.toResponseDto(userService.createUser(userDto, Set.of(UserType.TEACHER)));
    }

    public UserResponseDto registerStudent(UserDto userDto) {
        userValidator.validateUserRegistration(userDto);
        return mapper.toResponseDto(userService.createUser(userDto, Set.of(UserType.STUDENT)));
    }

    public JwtDto createSession(UserCredentialsDto userCredentialsDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userCredentialsDto.getUsername(),
                        userCredentialsDto.getPassword()
                )
        );

        User user = userService.getUserByUsername(userCredentialsDto.getUsername());
        String jwt = jwtProvider.generateJwt(user);

        return new JwtDto(jwt);
    }
}
