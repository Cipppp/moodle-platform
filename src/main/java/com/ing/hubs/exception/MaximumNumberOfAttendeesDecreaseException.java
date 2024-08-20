package com.ing.hubs.exception;

import org.springframework.http.HttpStatus;

public class MaximumNumberOfAttendeesDecreaseException extends MoodleException{
    public MaximumNumberOfAttendeesDecreaseException() {
        super(HttpStatus.BAD_REQUEST, "New maximum number of attendees cannot be lower than the current one");
    }
}
