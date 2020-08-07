package com.example.demo.common.exception;

import org.springframework.security.core.AuthenticationException;

public class KeyExpiredException extends AuthenticationException {

    public KeyExpiredException(String msg, Throwable t) {
        super(msg, t);
    }

    public KeyExpiredException(String msg) {
        super(msg);
    }
}
