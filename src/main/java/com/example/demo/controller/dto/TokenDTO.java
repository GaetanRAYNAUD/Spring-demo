package com.example.demo.controller.dto;

import com.example.demo.model.AuthMethod;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(name = "Token")
public class TokenDTO {

    private String token;

    private Date expiresAt;

    private AuthMethod authMethod;

    public TokenDTO(String token, Date expiresAt, AuthMethod authMethod) {
        this.token = token;
        this.expiresAt = expiresAt;
        this.authMethod = authMethod;
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

    public AuthMethod getAuthMethod() {
        return authMethod;
    }

    public void setAuthMethod(AuthMethod authMethod) {
        this.authMethod = authMethod;
    }
}
