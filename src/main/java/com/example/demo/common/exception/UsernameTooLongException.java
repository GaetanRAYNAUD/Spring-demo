package com.example.demo.common.exception;

public class UsernameTooLongException extends RuntimeException {

    public UsernameTooLongException() {
    }

    public UsernameTooLongException(String message) {
        super(message);
    }

    public UsernameTooLongException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameTooLongException(Throwable cause) {
        super(cause);
    }

    public UsernameTooLongException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
