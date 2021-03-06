package com.example.demo.common.exception;

import org.springframework.security.core.AuthenticationException;

public class UserAlreadyExistException extends AuthenticationException {

    public UserAlreadyExistException(String msg, Throwable t) {
        super(msg, t);
    }

    public UserAlreadyExistException(String msg) {
        super(msg);
    }
}
