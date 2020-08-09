package com.example.demo.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "GoogleUserRegistration")
public class GoogleUserRegistrationDTO {

    private String username;

    private String tokenId;

    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
