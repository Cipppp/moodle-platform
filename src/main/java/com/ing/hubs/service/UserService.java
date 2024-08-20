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
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

import static java.util.Optional.ofNullable;

@Service
@AllArgsConstructor
@Transactional
public class UserService {
    private UserValidator userValidator;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private Mapper mapper;

    public User createUser(UserDto userDto, Set<UserType> userTypes) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User user = mapper.fromDto(userDto, userTypes);
        return userRepository.save(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    public UserResponseDto getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return mapper.toResponseDto(getUserById(user.getId()));
    }

    public UserResponseDto updateCurrentUser(UserUpdateDto userUpdateDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        validateUserDto(userUpdateDto, user);

        ofNullable(userUpdateDto.getUsername()).ifPresent((user::setUsername));
        ofNullable(userUpdateDto.getFullName()).ifPresent(user::setFullName);
        ofNullable(userUpdateDto.getPhoneNumber()).ifPresent(user::setPhoneNumber);
        ofNullable(userUpdateDto.getEmail()).ifPresent(user::setEmail);
        ofNullable(userUpdateDto.getPassword()).ifPresent(password -> user.setPassword(passwordEncoder.encode(password)));

        return mapper.toResponseDto(userRepository.save(user));
    }

    private void validateUserDto(UserUpdateDto userUpdateDto, User user) {
        if (!Objects.equals(userUpdateDto.getUsername(), user.getUsername())){
            userValidator.validateUsernameDuplication(userUpdateDto.getUsername());
        }
        if (!Objects.equals(userUpdateDto.getEmail(), user.getEmail())) {
            userValidator.validateEmailDuplication(userUpdateDto.getEmail());
        }
    }
}
