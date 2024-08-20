package com.ing.hubs.exception;

import org.springframework.http.HttpStatus;

public class BadEnrollmentRequestStatusException extends MoodleException {
    public BadEnrollmentRequestStatusException() {
        super(HttpStatus.BAD_REQUEST, "Cannot delete enrollment request with status other than PENDING");
    }
}
