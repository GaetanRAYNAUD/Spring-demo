package com.example.demo.service.google.objects;

import java.net.URL;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.List;

public class GoogleOpenIdMetaData {

    private LocalDateTime configurationExpiration;

    private URL jwksUri;

    private List<String> issuers;

    private LocalDateTime keysExpiration;

    private List<PublicKey> publicKeys;

    public LocalDateTime getConfigurationExpiration() {
        return configurationExpiration;
    }

    public void setConfigurationExpiration(LocalDateTime configurationExpiration) {
        this.configurationExpiration = configurationExpiration;
    }

    public URL getJwksUri() {
        return jwksUri;
    }

    public void setJwksUri(URL jwksUri) {
        this.jwksUri = jwksUri;
    }

    public List<String> getIssuers() {
        return issuers;
    }

    public void setIssuers(List<String> issuers) {
        this.issuers = issuers;
    }

    public LocalDateTime getKeysExpiration() {
        return keysExpiration;
    }

    public void setKeysExpiration(LocalDateTime keysExpiration) {
        this.keysExpiration = keysExpiration;
    }

    public List<PublicKey> getPublicKeys() {
        return publicKeys;
    }

    public void setPublicKeys(List<PublicKey> publicKeys) {
        this.publicKeys = publicKeys;
    }
}
