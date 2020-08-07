package com.example.demo.service;

import com.example.demo.model.User;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.tuple.Pair;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public interface JwtService {

    void authenticateFromRequest(HttpServletRequest request);

    Pair<String, Date> generateToken(User user);

    Claims getClaims(String bearerToken);

    Claims getGoogleClaims(String token);
}
