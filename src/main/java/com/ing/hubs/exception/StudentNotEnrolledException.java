package com.ing.hubs.exception;

import org.springframework.http.HttpStatus;

public class StudentNotEnrolledException extends MoodleException {
    public StudentNotEnrolledException() {
        super(HttpStatus.NOT_FOUND, "Student not enrolled");
    }
}
