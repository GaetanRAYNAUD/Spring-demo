package com.example.demo.common.exception;

import org.springframework.security.core.AuthenticationException;

public class NotGoogleAccountException extends AuthenticationException {

    public NotGoogleAccountException(String msg, Throwable t) {
        super(msg, t);
    }

    public NotGoogleAccountException(String msg) {
        super(msg);
    }
}
