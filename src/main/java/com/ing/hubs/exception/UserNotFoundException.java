package com.ing.hubs.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends MoodleException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, "User not found");
    }
}
