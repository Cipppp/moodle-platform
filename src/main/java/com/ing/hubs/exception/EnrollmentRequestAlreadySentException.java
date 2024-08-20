package com.ing.hubs.exception;

import org.springframework.http.HttpStatus;

public class EnrollmentRequestAlreadySentException extends MoodleException {
    public EnrollmentRequestAlreadySentException() {
        super(HttpStatus.BAD_REQUEST, "Enrollment request already sent to this course");
    }
}
