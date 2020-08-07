package com.example.demo.config.properties;

import java.net.URI;

public class GoogleOpenIdProperties {

    private String clientId;

    private URI configurationUrl;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public URI getConfigurationUrl() {
        return configurationUrl;
    }

    public void setConfigurationUrl(URI configurationUrl) {
        this.configurationUrl = configurationUrl;
    }
}
