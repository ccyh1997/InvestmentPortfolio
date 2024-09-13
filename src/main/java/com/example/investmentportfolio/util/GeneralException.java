package com.example.investmentportfolio.util;

import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {
    private final transient CustomError error;

    public GeneralException(CustomError error) {
        this.error = error;
    }

}