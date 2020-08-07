package com.example.demo.config.properties;

public class SecurityJwtProperties {

    private String secretKey;

    private String publicKey;

    private Integer expiration;

    private String issuer;

    private Integer activationExpiration;

    private String activationPath;

    private Integer resetExpiration;

    private String resetPath;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public Integer getExpiration() {
        return expiration;
    }

    public void setExpiration(Integer expiration) {
        this.expiration = expiration;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Integer getActivationExpiration() {
        return activationExpiration;
    }

    public void setActivationExpiration(Integer activationExpiration) {
        this.activationExpiration = activationExpiration;
    }

    public String getActivationPath() {
        return activationPath;
    }

    public void setActivationPath(String activationPath) {
        this.activationPath = activationPath;
    }

    public Integer getResetExpiration() {
        return resetExpiration;
    }

    public void setResetExpiration(Integer resetExpiration) {
        this.resetExpiration = resetExpiration;
    }

    public String getResetPath() {
        return resetPath;
    }

    public void setResetPath(String resetPath) {
        this.resetPath = resetPath;
    }
}
