package com.ing.hubs.exception;

import org.springframework.http.HttpStatus;

public class MaximumNumberOfAttendeesReachedException extends MoodleException{
    public MaximumNumberOfAttendeesReachedException() {
        super(HttpStatus.BAD_REQUEST, "Maximum number of students reached");
    }
}