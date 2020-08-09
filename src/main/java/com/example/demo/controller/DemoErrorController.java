package com.example.demo.controller;

import com.example.demo.controller.object.ErrorCode;
import com.example.demo.controller.object.ErrorObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

@Component
public class DemoErrorController extends BasicErrorController {

    public DemoErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes, new ErrorProperties());
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ErrorObject<Void>> handleError(HttpServletResponse response) {
        if (StringUtils.isNotBlank(response.getHeader(HttpHeaders.WWW_AUTHENTICATE))) { //Don't override basic auth
            return null;
        }

        switch (response.getStatus()) {
            case 401:
                return new ResponseEntity<>(new ErrorObject<>(ErrorCode.NOT_AUTHENTICATED), HttpStatus.FORBIDDEN);
            case 403:
                return new ResponseEntity<>(new ErrorObject<>(ErrorCode.NOT_AUTHORIZED), HttpStatus.FORBIDDEN);
            case 404:
                return new ResponseEntity<>(new ErrorObject<>(ErrorCode.NOT_FOUND), HttpStatus.NOT_FOUND);
            case 500:
            default:
                return new ResponseEntity<>(new ErrorObject<>(ErrorCode.DEFAULT_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
