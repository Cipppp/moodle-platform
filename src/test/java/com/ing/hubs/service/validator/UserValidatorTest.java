package com.ing.hubs.service.validator;

import com.ing.hubs.dto.UserDto;
import com.ing.hubs.exception.CredentialCombinationAlreadyInUseException;
import com.ing.hubs.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserValidatorTest {

    private UserRepository userRepository;
    private UserValidator userValidator;

    @BeforeEach
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        userValidator = new UserValidator(userRepository);
    }

    @Nested
    class ValidateUserRegistration {
        @Test
        void credentialCombinationInUse() {
            UserDto userDto = UserDto.builder()
                    .username("username")
                    .email("email")
                    .build();

            when(userRepository.existsByUsernameOrEmail(
                    userDto.getUsername(),
                    userDto.getEmail())).thenReturn(true);

            assertThrows(CredentialCombinationAlreadyInUseException.class, () ->
                    userValidator.validateUserRegistration(userDto));

            verify(userRepository).existsByUsernameOrEmail(
                    userDto.getUsername(),
                    userDto.getEmail());
        }

        @Test
        void credentialCombinationNotInUse() {
            UserDto userDto = UserDto.builder()
                    .username("username")
                    .email("email")
                    .build();

            when(userRepository.existsByUsernameOrEmail(
                    userDto.getUsername(),
                    userDto.getEmail())).thenReturn(false);

            assertDoesNotThrow(() ->
                    userValidator.validateUserRegistration(userDto));

            verify(userRepository).existsByUsernameOrEmail(
                    userDto.getUsername(),
                    userDto.getEmail());
        }
    }

    @Test
    public void validateUsernameDuplication() {
        when(userRepository.existsByUsername("existingUsername")).thenReturn(true);

        assertThrows(CredentialCombinationAlreadyInUseException.class, () -> {
            userValidator.validateUsernameDuplication("existingUsername");
        });

        verify(userRepository).existsByUsername("existingUsername");
    }

    @Test
    public void validateEmailDuplication() {
        when(userRepository.existsByEmail("existingEmail@example.com")).thenReturn(true);

        assertThrows(CredentialCombinationAlreadyInUseException.class, () -> {
            userValidator.validateEmailDuplication("existingEmail@example.com");
        });

        verify(userRepository).existsByEmail("existingEmail@example.com");
    }

}