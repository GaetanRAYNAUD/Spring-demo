package com.example.demo.common.exception;

import org.springframework.security.core.AuthenticationException;

public class NoGoogleAccountException extends AuthenticationException {

    public NoGoogleAccountException(String msg, Throwable t) {
        super(msg, t);
    }

    public NoGoogleAccountException(String msg) {
        super(msg);
    }
}
