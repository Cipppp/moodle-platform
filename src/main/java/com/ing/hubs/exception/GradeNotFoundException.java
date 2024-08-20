package com.ing.hubs.exception;

import org.springframework.http.HttpStatus;

public class GradeNotFoundException extends MoodleException {
    public GradeNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Grade not found");
    }
}
