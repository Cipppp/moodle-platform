package com.ing.hubs.service;

import com.ing.hubs.dto.JwtDto;
import com.ing.hubs.dto.UserCredentialsDto;
import com.ing.hubs.dto.UserDto;
import com.ing.hubs.dto.UserResponseDto;
import com.ing.hubs.entity.User;
import com.ing.hubs.entity.UserType;
import com.ing.hubs.exception.UserNotFoundException;
import com.ing.hubs.mapper.Mapper;
import com.ing.hubs.security.JwtProvider;
import com.ing.hubs.service.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserService userService;
    private JwtProvider jwtProvider;
    private AuthenticationManager authenticationManager;
    private final Mapper mapper = new Mapper(new ModelMapper());

    private AuthService authService;

    @BeforeEach
    public void setup() {
        userService = mock(UserService.class);
        jwtProvider = mock(JwtProvider.class);
        UserValidator userValidator = mock(UserValidator.class);
        authenticationManager = mock(AuthenticationManager.class);
        authService = new AuthService(
                userService,
                jwtProvider,
                authenticationManager,
                userValidator,
                mapper
        );
    }

    @Test
    void shouldRegisterTeacherSuccessfully() {
        UserDto userDto = new UserDto("Spike", "Spike cat", "spike@gmail.com", "0774657614", "spike123");
        User user = User.builder()
                .username("Spike")
                .fullName("Spike cat")
                .email("spike@gmail.com")
                .phoneNumber("0774657614")
                .password("spike123")
                .roles(Set.of(UserType.TEACHER))
                .build();
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .username("Spike")
                .fullName("Spike cat")
                .email("spike@gmail.com")
                .phoneNumber("0774657614")
                .roles(Set.of(UserType.TEACHER))
                .build();

        when(userService.createUser(userDto, Set.of(UserType.TEACHER))).thenReturn(user);

        UserResponseDto result = authService.registerTeacher(userDto);

        assertNotNull(result);
        assertEquals("Spike cat", result.getFullName());
        verify(userService).createUser(userDto, Set.of(UserType.TEACHER));
    }

    @Test
    void shouldRegisterStudentSuccessfully() {
        UserDto userDto = new UserDto("Spike", "Spike cat", "spike@gmail.com", "0774657614", "spike123");
        User student = User.builder()
                .username("Spike")
                .fullName("Spike cat")
                .email("spike@gmail.com")
                .phoneNumber("0774657614")
                .password("spike123")
                .roles(Set.of(UserType.STUDENT))
                .build();
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .username("Spike")
                .fullName("Spike cat")
                .email("spike@gmail.com")
                .phoneNumber("0774657614")
                .roles(Set.of(UserType.STUDENT))
                .build();

        when(userService.createUser(userDto, Set.of(UserType.STUDENT))).thenReturn(student);

        UserResponseDto result = authService.registerStudent(userDto);

        assertNotNull(result);
        assertEquals("Spike cat", result.getFullName());
        verify(userService).createUser(userDto, Set.of(UserType.STUDENT));
    }

    @Test
    void shouldCreateSessionSuccessfully() {
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("Spike", "spike123");
        User student = User.builder()
                .username("Spike")
                .password("spike123")
                .roles(Set.of(UserType.STUDENT))
                .build();
        String jwtToken = "dummy-token";

        when(userService.getUserByUsername("Spike")).thenReturn(student);
        when(jwtProvider.generateJwt(student)).thenReturn(jwtToken);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        JwtDto result = authService.createSession(userCredentialsDto);

        assertNotNull(result);
        assertEquals(jwtToken, result.getJwt());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtProvider).generateJwt(student);
    }

    @Test
    void shouldThrowExceptionWhenAuthenticationFails() {
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto("Spike", "spike123");
        when(userService.getUserByUsername("Spike")).thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class, () -> authService.createSession(userCredentialsDto));
    }
}