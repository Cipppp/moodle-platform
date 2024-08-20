package com.ing.hubs.exception;

import org.springframework.http.HttpStatus;

public class EnrollmentRequestNotFoundException extends MoodleException {
    public EnrollmentRequestNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Enrollment request not found");
    }
}
