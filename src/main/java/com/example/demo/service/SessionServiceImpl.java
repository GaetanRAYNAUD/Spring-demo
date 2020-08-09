package com.example.demo.service;

import com.example.demo.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SessionServiceImpl implements SessionService {

    private final UserDetailsService userService;

    public SessionServiceImpl(UserDetailsService userService) {
        this.userService = userService;
    }

    @Override
    public User getCurrentUser() {
        return (User) getAuthentication().getPrincipal();
    }

    @Override
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication != null && authentication.isAuthenticated() &&
               !(authentication instanceof AnonymousAuthenticationToken);
    }

    @Override
    public void authenticateFromToken(Claims claims) {
        User user = (User) this.userService.loadUserByUsername(claims.getSubject());

        if (user.getGoogleUser() == null && user.getPasswordResetDate().after(claims.getIssuedAt())) {
            throw new ExpiredJwtException(null, claims,
                                          "Someone tried to use a jwt token issued before last password change for: "
                                          + claims.getSubject());
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(user,
                                                                                "",
                                                                                user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("User is not Authenticated");
        }

        return authentication;
    }
}
