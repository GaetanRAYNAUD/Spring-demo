package com.example.demo.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ResendActivation")
public class ResendActivationDTO extends EmailDTO {

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
