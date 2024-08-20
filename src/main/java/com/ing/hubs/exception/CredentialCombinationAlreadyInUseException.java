package com.ing.hubs.exception;

import org.springframework.http.HttpStatus;

public class CredentialCombinationAlreadyInUseException extends MoodleException {
    public CredentialCombinationAlreadyInUseException() {
        super(HttpStatus.BAD_REQUEST, "Credential combination already in use");
    }
}
