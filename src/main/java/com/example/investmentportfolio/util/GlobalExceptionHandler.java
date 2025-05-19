package com.example.investmentportfolio.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

import static com.example.investmentportfolio.util.Constants.FORBIDDEN_ERROR_CODE;
import static com.example.investmentportfolio.util.Constants.FORBIDDEN_ERROR_MESSAGE;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<CustomError> handleValidationException(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getError());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomError> handleAccessDeniedException() {
        CustomError errorMessage = new CustomError(FORBIDDEN_ERROR_CODE, Collections.singletonList(FORBIDDEN_ERROR_MESSAGE));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
    }
}

