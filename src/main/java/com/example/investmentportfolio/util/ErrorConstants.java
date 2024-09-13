package com.example.investmentportfolio.util;

public class ErrorConstants {
    public static final String BAD_REQUEST_ERROR_CODE = "400 Bad Request";
    public static final String NOT_FOUND_ERROR_CODE = "404 Not Found";
    public static final String INTERNAL_SERVER_ERROR_ERROR_CODE = "500 Internal Server Error";

    private ErrorConstants() {
        throw new AssertionError("Constants class should not be instantiated.");
    }
}
