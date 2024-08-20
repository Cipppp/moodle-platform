package com.ing.hubs.exception;

import org.springframework.http.HttpStatus;

public class InvalidScheduleException extends MoodleException {
    public InvalidScheduleException() {
        super(HttpStatus.BAD_REQUEST, "Invalid schedule");
    }
}
