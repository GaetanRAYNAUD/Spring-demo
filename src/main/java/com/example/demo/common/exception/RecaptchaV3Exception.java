package com.example.demo.common.exception;

public class RecaptchaV3Exception extends RuntimeException {
    public RecaptchaV3Exception() {
    }

    public RecaptchaV3Exception(String message) {
        super(message);
    }

    public RecaptchaV3Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public RecaptchaV3Exception(Throwable cause) {
        super(cause);
    }

    public RecaptchaV3Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
