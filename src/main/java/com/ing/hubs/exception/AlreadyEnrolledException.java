package com.ing.hubs.exception;

import org.springframework.http.HttpStatus;

public class AlreadyEnrolledException extends MoodleException {
    public AlreadyEnrolledException() {
        super(HttpStatus.BAD_REQUEST, "Student has already sent a request for this course");
    }
}
