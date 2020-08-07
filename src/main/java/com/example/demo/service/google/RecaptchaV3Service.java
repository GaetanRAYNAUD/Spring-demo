package com.example.demo.service.google;

public interface RecaptchaV3Service {
    void validateToken(String token, String login, RecaptchaV3Action action);
}
