package com.example.demo.common.exception;

import org.springframework.security.core.AuthenticationException;

public class KeyNotFoundException extends AuthenticationException {

    public KeyNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public KeyNotFoundException(String msg) {
        super(msg);
    }
}
