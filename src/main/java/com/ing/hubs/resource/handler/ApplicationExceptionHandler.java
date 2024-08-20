package com.ing.hubs.resource.handler;

import com.ing.hubs.exception.MoodleException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(value = { MoodleException.class })
    protected ResponseEntity<ExceptionBody> handleException(MoodleException exception) {
        var body = new ExceptionBody(exception.getMessage());
        return new ResponseEntity<>(body, exception.getHttpStatus());
    }

    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ExceptionBody handleException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return new ExceptionBody(processFieldErrors(fieldErrors));
    }

    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ExceptionBody handleException(HttpMessageNotReadableException ex) {
        String message = ex.getMessage();
        return new ExceptionBody(message);
    }

    @ResponseStatus(FORBIDDEN)
    @ResponseBody
    @ExceptionHandler(AccessDeniedException.class)
    public ExceptionBody handleException(AccessDeniedException ex) {
        String message = ex.getMessage();
        return new ExceptionBody(message);
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ExceptionBody handleException(Exception ex) {
        String message = ex.getMessage();
        return new ExceptionBody(message);
    }

    private String processFieldErrors(List<org.springframework.validation.FieldError> fieldErrors) {
        return "validation error: " +
                fieldErrors
                        .stream()
                        .map(fieldError ->
                                fieldError.getField() +
                                " " +
                                fieldError.getDefaultMessage())
                        .collect(Collectors.joining(", "));
    }

    @Data
    @AllArgsConstructor
    public static class ExceptionBody {
        private String message;
    }
}