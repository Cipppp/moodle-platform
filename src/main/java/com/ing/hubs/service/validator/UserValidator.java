package com.ing.hubs.service.validator;

import com.ing.hubs.dto.UserDto;
import com.ing.hubs.exception.CredentialCombinationAlreadyInUseException;
import com.ing.hubs.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserValidator {
    private UserRepository userRepository;

    public void validateUserRegistration(UserDto userDto) {
        if (userRepository.existsByUsernameOrEmail(userDto.getUsername(), userDto.getEmail())) {
            throw new CredentialCombinationAlreadyInUseException();
        }
    }

    public void validateUsernameDuplication(String username ) {
        if (userRepository.existsByUsername(username)) {
            throw new CredentialCombinationAlreadyInUseException();
        }
    }

    public void validateEmailDuplication(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CredentialCombinationAlreadyInUseException();
        }
    }
}
