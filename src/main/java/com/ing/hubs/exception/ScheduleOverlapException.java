package com.ing.hubs.exception;

import org.springframework.http.HttpStatus;

public class ScheduleOverlapException extends MoodleException {
    public ScheduleOverlapException() {
        super(HttpStatus.BAD_REQUEST, "Schedule overlaps with other schedule");
    }
}