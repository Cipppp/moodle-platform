package com.ing.hubs.exception;

import org.springframework.http.HttpStatus;

public class StudentAlreadyGradedException extends MoodleException {
    public StudentAlreadyGradedException() {
        super(HttpStatus.BAD_REQUEST, "Student already graded");
    }
}