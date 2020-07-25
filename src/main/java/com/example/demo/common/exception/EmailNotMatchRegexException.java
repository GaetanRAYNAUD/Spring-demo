package com.example.demo.common.exception;

public class EmailNotMatchRegexException extends RuntimeException {

    public EmailNotMatchRegexException() {
    }

    public EmailNotMatchRegexException(String message) {
        super(message);
    }

    public EmailNotMatchRegexException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailNotMatchRegexException(Throwable cause) {
        super(cause);
    }

    public EmailNotMatchRegexException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
