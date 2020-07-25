package com.example.demo.service.google;

public class RecaptchaV3VerificationBody {

    private String secret;

    private String response;

    public RecaptchaV3VerificationBody(String secret, String response) {
        this.secret = secret;
        this.response = response;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
