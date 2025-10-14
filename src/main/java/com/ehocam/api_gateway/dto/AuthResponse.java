package com.ehocam.api_gateway.dto;

public class AuthResponse {
    
    private AuthTokens tokens;
    private UserInfo user;
    
    // Constructors
    public AuthResponse() {}
    
    public AuthResponse(AuthTokens tokens, UserInfo user) {
        this.tokens = tokens;
        this.user = user;
    }
    
    // Getters and Setters
    public AuthTokens getTokens() {
        return tokens;
    }
    
    public void setTokens(AuthTokens tokens) {
        this.tokens = tokens;
    }
    
    public UserInfo getUser() {
        return user;
    }
    
    public void setUser(UserInfo user) {
        this.user = user;
    }
}
