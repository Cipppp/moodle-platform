package com.ing.hubs.service;

import com.ing.hubs.dto.UserDto;
import com.ing.hubs.dto.UserResponseDto;
import com.ing.hubs.dto.UserUpdateDto;
import com.ing.hubs.entity.User;
import com.ing.hubs.entity.UserType;
import com.ing.hubs.exception.UserNotFoundException;
import com.ing.hubs.mapper.Mapper;
import com.ing.hubs.repository.UserRepository;
import com.ing.hubs.service.validator.UserValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserValidator userValidator;

    private UserService userService;

    Authentication authentication;

    @BeforeEach
    public void setup() {
        userService = new UserService(userValidator, userRepository, passwordEncoder, new Mapper(new ModelMapper()));

        authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
    @Nested
    class CreateUser {
        @Test
        void whenCreateUser_ReturnsSavedUser() {
            UserDto userDto = new UserDto("Spike", "Spike cat", "spike@gmail.com", "0774657614", "spike123");
            User user = User.builder()
                    .username("Spike")
                    .fullName("Spike cat")
                    .email("spike@gmail.com")
                    .phoneNumber("0774657614")
                    .password("spike_encoded")
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            User savedUser = User.builder()
                    .id(1L)
                    .username("Spike")
                    .fullName("Spike cat")
                    .email("spike@gmail.com")
                    .phoneNumber("0774657614")
                    .password("spike_encoded")
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            when(userRepository.save(user)).thenReturn(savedUser);
            when(passwordEncoder.encode("spike123")).thenReturn("spike_encoded");

            User result = userService.createUser(userDto, Set.of(UserType.STUDENT));

            assertEquals(savedUser, result);
            verify(userRepository).save(user);
        }
    }

    @Nested
    class GetUserById {
        @Test
        void whenUserExists_ReturnsUser() {
            User user = User.builder()
                    .id(1L)
                    .username("Spike")
                    .fullName("Spike cat")
                    .email("spike@gmail.com")
                    .phoneNumber("0774657614")
                    .password("spike123")
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            User result = userService.getUserById(1L);

            assertEquals(user, result);
        }

        @Test
        void whenUserDoesNotExist_ThrowsException() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
        }
    }

    @Nested
    class GetUserByUsername {
        @Test
        void whenUserExists_ReturnsUser() {
            User user = User.builder()
                    .username("Spike")
                    .fullName("Spike cat")
                    .email("spike@gmail.com")
                    .phoneNumber("0774657614")
                    .password("spike123")
                    .build();

            when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

            User result = userService.getUserByUsername(user.getUsername());

            assertEquals(user, result);
        }

        @Test
        void whenUserDoesNotExist_ThrowsException() {
            when(userRepository.findByUsername("spike")).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername("spike"));
        }
    }

    @Nested
    class GetCurrentUser {
        @Test
        void returnsCurrentUser() {
            User user = User.builder()
                    .id(1L)
                    .username("Spike")
                    .fullName("Spike cat")
                    .email("spike@gmail.com")
                    .phoneNumber("0774657614")
                    .password("spike123")
                    .build();
            UserResponseDto responseDto = UserResponseDto.builder()
                    .id(1L)
                    .username("Spike")
                    .fullName("Spike cat")
                    .email("spike@gmail.com")
                    .phoneNumber("0774657614")
                    .build();

            setLoggedInUser(user);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            UserResponseDto result = userService.getCurrentUser();

            assertEquals(responseDto, result);
        }
    }

    @Nested
    class UpdateCurrentUser {
        @Test
        void whenValidUpdate_ReturnsUpdatedUser() {
            UserUpdateDto updateDto = new UserUpdateDto("spikey", "spike cat", "spike@gmail.com", "0774657614", "spike");
            User user = User.builder()
                    .id(1L)
                    .username("Spike")
                    .password("spike1234")
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            User updatedUser = User.builder()
                    .id(1L)
                    .username("spikey")
                    .fullName("spike cat")
                    .phoneNumber("0774657614")
                    .email("spike@gmail.com")
                    .password("spike_encoded")
                    .roles(Set.of(UserType.STUDENT))
                    .build();

            setLoggedInUser(user);

            when(passwordEncoder.encode("spike")).thenReturn("spike_encoded");
            when(userRepository.save(updatedUser)).thenReturn(updatedUser);

            doNothing().when(userValidator).validateUsernameDuplication(anyString());
            doNothing().when(userValidator).validateEmailDuplication(anyString());

            UserResponseDto responseDto = UserResponseDto.builder()
                    .id(1L)
                    .username("spikey")
                    .fullName("spike cat")
                    .phoneNumber("0774657614")
                    .email("spike@gmail.com")
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            UserResponseDto result = userService.updateCurrentUser(updateDto);

            assertEquals(responseDto, result);
            assertEquals("spikey", user.getUsername());
            assertEquals("spike_encoded", user.getPassword());
            verify(userRepository).save(user);
        }
    }

    private void setLoggedInUser(User user) {
        when(authentication.getPrincipal()).thenReturn(user);
    }
}
