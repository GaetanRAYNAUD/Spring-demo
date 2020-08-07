package com.example.demo.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserActivation")
public class UserActivationDTO {

    private String key;

    private String token;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
