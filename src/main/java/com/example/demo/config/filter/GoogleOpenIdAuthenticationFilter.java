package com.example.demo.config.filter;

import com.example.demo.common.Constants;
import com.example.demo.common.exception.NoGoogleAccountException;
import com.example.demo.common.exception.NotGoogleAccountException;
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
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class GoogleOpenIdAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String EMAIL_KEY = "email";

    public static final String GOOGLE_TOKEN_ID_KEY = "tokenId";

    public static final String FORM_RECAPTCHA_KEY = "token";

    private final JwtService jwtService;

    private final ObjectMapper objectMapper;

    private final ResetKeyService resetKeyService;

    private final RecaptchaV3Service recaptchaV3Service;

    public GoogleOpenIdAuthenticationFilter(AuthenticationManager authenticationManager, JwtService jwtService, ObjectMapper objectMapper,
                                            ResetKeyService resetKeyService, RecaptchaV3Service recaptchaV3Service) {
        super(new AntPathRequestMatcher("/public/authentication/login/google", "POST"));
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
        this.resetKeyService = resetKeyService;
        this.recaptchaV3Service = recaptchaV3Service;
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            this.recaptchaV3Service.validateToken(request.getParameter(FORM_RECAPTCHA_KEY), request.getParameter(EMAIL_KEY), RecaptchaV3Action.LOGIN);

            String email = request.getParameter(EMAIL_KEY);
            String tokenId = request.getParameter(GOOGLE_TOKEN_ID_KEY);

            GoogleAuthenticationToken authRequest = new GoogleAuthenticationToken(email, tokenId);

            return this.getAuthenticationManager().authenticate(authRequest);
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
        } catch (NoGoogleAccountException e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            try {
                response.getWriter().write(this.objectMapper.writeValueAsString(new ErrorObject<>(ErrorCode.NO_GOOGLE_ACCOUNT)));
                response.flushBuffer();
            } catch (Exception e1) {
                throw e;
            }

            return null;
        } catch (NotGoogleAccountException e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            try {
                response.getWriter().write(this.objectMapper.writeValueAsString(new ErrorObject<>(ErrorCode.NOT_GOOGLE_ACCOUNT)));
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

        Pair<String, Date> token = this.jwtService.generateToken(user);
        response.addHeader(HttpHeaders.AUTHORIZATION, Constants.BEARER + " " + token);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(this.objectMapper.writeValueAsString(new TokenDTO(token.getKey(), token.getValue(), AuthMethod.GOOGLE)));
    }
}
