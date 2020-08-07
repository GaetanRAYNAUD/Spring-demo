package com.example.demo.service.google;

import io.jsonwebtoken.Claims;

public interface GoogleOpenIdService {
    Claims validateToken(String token);
}
