package com.example.demo.common.exception;

import org.springframework.security.core.AuthenticationException;

public class ResetPasswordException extends AuthenticationException {

    public ResetPasswordException(String msg, Throwable t) {
        super(msg, t);
    }

    public ResetPasswordException(String msg) {
        super(msg);
    }
}
