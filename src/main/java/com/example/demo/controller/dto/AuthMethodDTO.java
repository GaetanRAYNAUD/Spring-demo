package com.example.demo.controller.dto;

import com.example.demo.model.AuthMethod;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AuthMethod")
public class AuthMethodDTO {

    private AuthMethod authMethod;

    public AuthMethodDTO(AuthMethod authMethod) {
        this.authMethod = authMethod;
    }

    public AuthMethod getAuthMethod() {
        return authMethod;
    }

    public void setAuthMethod(AuthMethod authMethod) {
        this.authMethod = authMethod;
    }
}
