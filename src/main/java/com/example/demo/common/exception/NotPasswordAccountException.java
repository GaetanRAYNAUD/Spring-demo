package com.example.demo.common.exception;

import org.springframework.security.core.AuthenticationException;

public class NotPasswordAccountException extends AuthenticationException {

    public NotPasswordAccountException(String msg, Throwable t) {
        super(msg, t);
    }

    public NotPasswordAccountException(String msg) {
        super(msg);
    }
}
