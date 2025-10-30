package com.ehocam.api_gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "google.oauth2")
public class GoogleOAuth2Config {
    
    private String webClientId;
    private String backofficeClientId;
    private String iosClientId;
    private String androidClientId;
    private String issuer;
    private String jwksUri;
    
    // Getters and Setters
    public String getWebClientId() {
        return webClientId;
    }
    
    public void setWebClientId(String webClientId) {
        this.webClientId = webClientId;
    }
    
    public String getBackofficeClientId() {
        return backofficeClientId;
    }
    
    public void setBackofficeClientId(String backofficeClientId) {
        this.backofficeClientId = backofficeClientId;
    }
    
    public String getIosClientId() {
        return iosClientId;
    }
    
    public void setIosClientId(String iosClientId) {
        this.iosClientId = iosClientId;
    }
    
    public String getAndroidClientId() {
        return androidClientId;
    }
    
    public void setAndroidClientId(String androidClientId) {
        this.androidClientId = androidClientId;
    }
    
    public String getIssuer() {
        return issuer;
    }
    
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
    
    public String getJwksUri() {
        return jwksUri;
    }
    
    public void setJwksUri(String jwksUri) {
        this.jwksUri = jwksUri;
    }
}
