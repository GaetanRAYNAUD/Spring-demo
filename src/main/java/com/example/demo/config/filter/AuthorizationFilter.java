package com.example.demo.config.filter;

import com.example.demo.controller.object.ErrorCode;
import com.example.demo.controller.object.ErrorObject;
import com.example.demo.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationFilter.class);

    private static final String IS_AUTHENTICATE_PATH = "/authentication/authenticated";

    private final JwtService jwtService;

    private final ObjectMapper objectMapper;

    public AuthorizationFilter(AuthenticationManager authenticationManager, JwtService jwtService, ObjectMapper objectMapper) {
        super(authenticationManager);
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.isAuthenticated()) {
                filterChain.doFilter(request, response);
            }

            this.jwtService.authenticateFromRequest(request);
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException exception) {
            SecurityContextHolder.clearContext();

            if (!request.getRequestURI().equals(IS_AUTHENTICATE_PATH)) { //Spamming
                LOGGER.warn("Someone tried to use an expired jwt token: {}", request.getHeader(HttpHeaders.AUTHORIZATION));
            }

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter()
                    .write(this.objectMapper.writeValueAsString(new ErrorObject<>(ErrorCode.NOT_AUTHENTICATED)));
            response.flushBuffer();
        } catch (JwtException | AuthenticationCredentialsNotFoundException exception) {
            SecurityContextHolder.clearContext();

            if (!request.getRequestURI().equals(IS_AUTHENTICATE_PATH)) {
                LOGGER.warn("Someone tried to use an invalid jwt token: {}", request.getHeader(HttpHeaders.AUTHORIZATION));
            }

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter()
                    .write(this.objectMapper.writeValueAsString(new ErrorObject<>(ErrorCode.NOT_AUTHENTICATED)));
            response.flushBuffer();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().startsWith("/public/");
    }
}
