package com.example.demo.config.filter;

import com.example.demo.common.exception.NoGoogleAccountException;
import com.example.demo.common.exception.NotGoogleAccountException;
import com.example.demo.service.JwtService;
import io.jsonwebtoken.Claims;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class GoogleAuthenticationProvider implements AuthenticationProvider {

    protected final Log logger = LogFactory.getLog(getClass());

    private GoogleUserDetailsService googleUserDetailsService;

    private final JwtService jwtService;

    public GoogleAuthenticationProvider(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        if (!authentication.getCredentials().getClass().equals(String.class)) {
            return authentication;
        }

        String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();

        Claims claims = jwtService.getGoogleClaims((String) authentication.getCredentials());

        GoogleUserDetails user;

        try {
            user = retrieveUser(username);
        } catch (UsernameNotFoundException e) {
            throw new NoGoogleAccountException("");
        }

        if (!user.getEmail().equals(claims.get("email"))) {
            throw new BadCredentialsException("Token and user don't have same email");
        }

        if (user.getGoogleId() == null) {
            throw new NotGoogleAccountException("User " + authentication.getPrincipal() + " is not a google user !");
        }

        if (!user.getGoogleId().equals(claims.getSubject())) {
            throw new BadCredentialsException("Token and user don't have same google id");
        }

        return new GoogleAuthenticationToken(user, "", user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (GoogleAuthenticationToken.class.isAssignableFrom(authentication));
    }

    protected final GoogleUserDetails retrieveUser(String username) {
        try {
            GoogleUserDetails loadedUser = this.getGoogleUserDetailsService().loadGoogleUserByUsername(username);

            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
            }

            return loadedUser;
        } catch (UsernameNotFoundException | InternalAuthenticationServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }
    }

    protected GoogleUserDetailsService getGoogleUserDetailsService() {
        return googleUserDetailsService;
    }

    public void setGoogleUserDetailsService(GoogleUserDetailsService googleUserDetailsService) {
        this.googleUserDetailsService = googleUserDetailsService;
    }
}
