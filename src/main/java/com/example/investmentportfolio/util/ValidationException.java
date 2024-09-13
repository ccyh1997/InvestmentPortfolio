package com.example.investmentportfolio.util;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final transient CustomError error;

    public ValidationException(CustomError error) {
        this.error = error;
    }

}
