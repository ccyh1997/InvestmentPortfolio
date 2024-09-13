package com.example.investmentportfolio.util;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final transient CustomError error;

    public NotFoundException(CustomError error) {
        this.error = error;
    }

}