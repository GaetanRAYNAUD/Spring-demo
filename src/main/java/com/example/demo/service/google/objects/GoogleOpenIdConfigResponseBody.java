package com.example.demo.service.google.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleOpenIdConfigResponseBody {

    @JsonProperty(value = "jwks_uri", required = true)
    private URL jwksUri;

    @JsonProperty(value = "issuer", required = true)
    private URL issuer;

    public URL getJwksUri() {
        return jwksUri;
    }

    public void setJwksUri(URL jwksUri) {
        this.jwksUri = jwksUri;
    }

    public URL getIssuer() {
        return issuer;
    }

    public void setIssuer(URL issuer) {
        this.issuer = issuer;
    }
}
