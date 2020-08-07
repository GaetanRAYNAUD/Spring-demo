package com.example.demo.common.exception;

import org.springframework.security.core.AuthenticationException;

public class EmailAlreadyExistException extends AuthenticationException {

    public EmailAlreadyExistException(String msg, Throwable t) {
        super(msg, t);
    }

    public EmailAlreadyExistException(String msg) {
        super(msg);
    }
}
