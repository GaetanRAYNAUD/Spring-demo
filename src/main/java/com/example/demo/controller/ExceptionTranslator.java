package com.example.demo.controller;

import com.example.demo.common.exception.EmailAlreadyExistException;
import com.example.demo.common.exception.EmailNotMatchRegexException;
import com.example.demo.common.exception.KeyExpiredException;
import com.example.demo.common.exception.KeyNotFoundException;
import com.example.demo.common.exception.PasswordsNotMatchException;
import com.example.demo.common.exception.PasswordsNotMatchRegexException;
import com.example.demo.common.exception.RecaptchaV3Exception;
import com.example.demo.common.exception.ResetPasswordException;
import com.example.demo.common.exception.UserAlreadyExistException;
import com.example.demo.common.exception.UsernameTooLongException;
import com.example.demo.common.exception.UsernameTooShortException;
import com.example.demo.controller.object.ErrorCode;
import com.example.demo.controller.object.ErrorObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

@ControllerAdvice
public class ExceptionTranslator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionTranslator.class);

    @ExceptionHandler
    public ResponseEntity<ErrorObject<Void>> handleException(Exception e) {
        LOGGER.error(e.getMessage(), e);

        return new ResponseEntity<>(new ErrorObject<>(ErrorCode.DEFAULT_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject<Void>> handleInterruptedException(InterruptedException e) {
        LOGGER.error(e.getMessage(), e);

        return new ResponseEntity<>(new ErrorObject<>(ErrorCode.DEFAULT_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject<Void>> handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException e) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(e.getMessage(), e);
        }

        return new ResponseEntity<>(new ErrorObject<>(ErrorCode.NOT_AUTHENTICATED), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject<Void>> handleAuthenticationCredentialsNotFoundException(AccessDeniedException e, HttpServletRequest request) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("{} tried to do an action it not authorized to: {} !", SecurityContextHolder.getContext()
                                                                                                    .getAuthentication()
                                                                                                    .getPrincipal(),
                        request.getRequestURI());
        }

        return new ResponseEntity<>(new ErrorObject<>(ErrorCode.NOT_AUTHORIZED), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject<Void>> handleBadCredentialsException(BadCredentialsException e) {
        return new ResponseEntity<>(new ErrorObject<>(ErrorCode.INVALID_CREDENTIALS), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject<Void>> handleValidationException(ValidationException e) {
        return new ResponseEntity<>(new ErrorObject<>(ErrorCode.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<BindingResult> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(e.getBindingResult(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject<Void>> handleHttpClientErrorExceptionBadRequest(HttpClientErrorException.BadRequest e) {
        return new ResponseEntity<>(new ErrorObject<>(ErrorCode.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject<Void>> handleUserAlreadyExistAuthenticationException(UserAlreadyExistException e) {
        return new ResponseEntity<>(new ErrorObject<>(ErrorCode.USER_ALREADY_EXIST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject<Void>> handleEmailAlreadyExistException(EmailAlreadyExistException e) {
        return new ResponseEntity<>(new ErrorObject<>(ErrorCode.EMAIL_ALREADY_EXIST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject<Void>> handlePasswordsNotMatchException(PasswordsNotMatchException e) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(e.getMessage());
        }

        return new ResponseEntity<>(new ErrorObject<>(ErrorCode.PASSWORDS_NOT_MATCH), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject<Void>> handlePasswordsNotMatchRegexException(PasswordsNotMatchRegexException e) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(e.getMessage());
        }

        return new ResponseEntity<>(new ErrorObject<>(ErrorCode.PASSWORD_NOT_MATCH_REGEX), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject<Void>> handleEmailNotMatchRegexException(EmailNotMatchRegexException e) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(e.getMessage());
        }

        return new ResponseEntity<>(new ErrorObject<>(ErrorCode.EMAIL_NOT_MATCH_REGEX), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject<Void>> handleUsernameTooLongException(UsernameTooLongException e) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(e.getMessage());
        }

        return new ResponseEntity<>(new ErrorObject<>(ErrorCode.USERNAME_TOO_LONG), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject<Void>> handleUsernameTooShortException(UsernameTooShortException e) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(e.getMessage());
        }

        return new ResponseEntity<>(new ErrorObject<>(ErrorCode.USERNAME_TOO_SHORT), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject<Void>> handleKeyNotFoundException(KeyNotFoundException e) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(e.getMessage());
        }

        return new ResponseEntity<>(new ErrorObject<>(ErrorCode.KEY_NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject<Void>> handleKeyExpiredException(KeyExpiredException e) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(e.getMessage());
        }

        return new ResponseEntity<>(new ErrorObject<>(ErrorCode.KEY_EXPIRED), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<Void> handleResetPasswordException(ResetPasswordException e) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject<Void>> handleRecaptchaV3Exception(RecaptchaV3Exception e) {
        LOGGER.error("[RECAPTCHA] - " + e.getMessage(), e);

        return new ResponseEntity<>(new ErrorObject<>(ErrorCode.INVALID_CREDENTIALS), HttpStatus.UNAUTHORIZED);
    }
}
