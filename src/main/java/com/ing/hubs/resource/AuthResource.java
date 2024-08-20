package com.ing.hubs.resource;

import com.ing.hubs.dto.JwtDto;
import com.ing.hubs.dto.UserCredentialsDto;
import com.ing.hubs.dto.UserDto;
import com.ing.hubs.dto.UserResponseDto;
import com.ing.hubs.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthResource {
    private AuthService authService;

    @PostMapping("/register/student")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto registerStudent(@RequestBody @Valid UserDto userDto) {
        return authService.registerStudent(userDto);
    }

    @PostMapping("/register/teacher")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto registerTeacher(@RequestBody @Valid UserDto userDto) {
        return authService.registerTeacher(userDto);
    }

    @PostMapping("/login")
    public JwtDto login(@RequestBody @Valid UserCredentialsDto userCredentialsDto) {
        return authService.createSession(userCredentialsDto);
    }

}
