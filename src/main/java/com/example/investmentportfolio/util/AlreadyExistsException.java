package com.example.investmentportfolio.util;

import lombok.Getter;

@Getter
public class AlreadyExistsException extends RuntimeException {
    private final transient CustomError error;

    public AlreadyExistsException(CustomError error) {
        this.error = error;
    }

}