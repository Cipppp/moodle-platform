package com.ing.hubs.exception;

import org.springframework.http.HttpStatus;

public class ScheduleNotFoundException extends MoodleException {
    public ScheduleNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Schedule not found");
    }
}
