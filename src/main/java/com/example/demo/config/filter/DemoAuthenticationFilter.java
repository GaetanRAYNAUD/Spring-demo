package com.example.demo.config.filter;

import com.example.demo.common.Constants;
import com.example.demo.common.exception.RecaptchaV3Exception;
import com.example.demo.controller.dto.TokenDTO;
import com.example.demo.controller.object.ErrorCode;
import com.example.demo.controller.object.ErrorObject;
import com.example.demo.model.AuthMethod;
import com.example.demo.model.User;
import com.example.demo.service.JwtService;
import com.example.demo.service.definition.ResetKeyService;
import com.example.demo.service.google.RecaptchaV3Action;
import com.example.demo.service.google.RecaptchaV3Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class DemoAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String FORM_RECAPTCHA_KEY = "token";

    private final JwtService jwtService;

    private final ObjectMapper objectMapper;

    private final ResetKeyService resetKeyService;

    private final RecaptchaV3Service recaptchaV3Service;

    public DemoAuthenticationFilter(AuthenticationManager authenticationManager, JwtService jwtService, ObjectMapper objectMapper,
                                    ResetKeyService resetKeyService,
                                    RecaptchaV3Service recaptchaV3Service) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
        this.resetKeyService = resetKeyService;
        this.recaptchaV3Service = recaptchaV3Service;
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            this.recaptchaV3Service.validateToken(request.getParameter(FORM_RECAPTCHA_KEY), request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY),
                                                  RecaptchaV3Action.LOGIN);
            return super.attemptAuthentication(request, response);
        } catch (AccountStatusException e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            try {
                response.getWriter().write(this.objectMapper.writeValueAsString(new ErrorObject<>(ErrorCode.ACCOUNT_NOT_ACTIVATED)));
                response.flushBuffer();
            } catch (Exception e1) {
                throw e;
            }

            return null;
        } catch (BadCredentialsException | RecaptchaV3Exception e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            try {
                response.getWriter().write(this.objectMapper.writeValueAsString(new ErrorObject<>(ErrorCode.INVALID_CREDENTIALS)));
                response.flushBuffer();
            } catch (Exception e1) {
                throw e;
            }

            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication auth) throws IOException {
        User user = (User) auth.getPrincipal();
        this.resetKeyService.deleteByUser(user);

        Pair<String, Date> token = this.jwtService.generateToken(user);
        response.addHeader(HttpHeaders.AUTHORIZATION, Constants.BEARER + " " + token);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(this.objectMapper.writeValueAsString(new TokenDTO(token.getKey(), token.getValue(), AuthMethod.PASSWORD)));
    }
}
