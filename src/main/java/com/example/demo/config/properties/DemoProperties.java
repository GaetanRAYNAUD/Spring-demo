package com.example.demo.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "demo")
public class DemoProperties {

    private String title;

    private String version;

    private String description;

    private String frontUrl;

    private String mailSender;

    private List<String> corsOrigin;

    @NestedConfigurationProperty
    private SecurityJwtProperties securityJwt;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrontUrl() {
        return frontUrl;
    }

    public void setFrontUrl(String frontUrl) {
        this.frontUrl = frontUrl;
    }

    public String getMailSender() {
        return mailSender;
    }

    public void setMailSender(String mailSender) {
        this.mailSender = mailSender;
    }

    public List<String> getCorsOrigin() {
        return corsOrigin;
    }

    public void setCorsOrigin(List<String> corsOrigin) {
        this.corsOrigin = corsOrigin;
    }

    public SecurityJwtProperties getSecurityJwt() {
        return securityJwt;
    }

    public void setSecurityJwt(SecurityJwtProperties securityJwt) {
        this.securityJwt = securityJwt;
    }
}
