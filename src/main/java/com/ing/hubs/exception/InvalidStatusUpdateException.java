package com.ing.hubs.exception;

import org.springframework.http.HttpStatus;

public class InvalidStatusUpdateException extends MoodleException{
    public InvalidStatusUpdateException() {
        super(HttpStatus.BAD_REQUEST, "Invalid status update");
    }
}
