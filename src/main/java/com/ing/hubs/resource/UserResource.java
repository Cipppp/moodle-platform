package com.ing.hubs.resource;

import com.ing.hubs.dto.UserResponseDto;
import com.ing.hubs.dto.UserUpdateDto;
import com.ing.hubs.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/me")
@AllArgsConstructor
public class UserResource {
    private UserService userService;

    @GetMapping
    public UserResponseDto getCurrentUser() {
        return userService.getCurrentUser();
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserResponseDto updateCurrentUser(@Valid @RequestBody UserUpdateDto userUpdateDto) {
        return userService.updateCurrentUser(userUpdateDto);
    }
}
