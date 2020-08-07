package com.example.demo.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(name = "Token")
public class TokenDTO {

    private String token;

    private Date expiresAt;

    public TokenDTO(String token, Date expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
}
