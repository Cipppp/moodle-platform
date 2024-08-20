package com.ing.hubs.exception;

import org.springframework.http.HttpStatus;

public class CourseNotFoundException extends MoodleException {
    public CourseNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Course not found");
    }
}
