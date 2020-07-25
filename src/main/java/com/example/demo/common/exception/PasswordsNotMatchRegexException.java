package com.example.demo.common.exception;

public class PasswordsNotMatchRegexException extends RuntimeException {

    public PasswordsNotMatchRegexException() {
    }

    public PasswordsNotMatchRegexException(String message) {
        super(message);
    }

    public PasswordsNotMatchRegexException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordsNotMatchRegexException(Throwable cause) {
        super(cause);
    }

    public PasswordsNotMatchRegexException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
