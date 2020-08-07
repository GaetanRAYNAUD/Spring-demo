package com.example.demo.service;

import com.example.demo.model.User;
import io.jsonwebtoken.Claims;

public interface SessionService {

    User getCurrentUser();

    boolean isAuthenticated();

    void authenticateFromToken(Claims claims);
}
