package com.ehocam.api_gateway.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthDto {

    public static class OAuth2Request {
        @NotBlank
        private String code;
        
        private String redirectUri;

        // Getters and Setters
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getRedirectUri() { return redirectUri; }
        public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }
    }

    public static class GuestRequest {
        private String fcmToken;
        private String platform;

        // Getters and Setters
        public String getFcmToken() { return fcmToken; }
        public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }
        public String getPlatform() { return platform; }
        public void setPlatform(String platform) { this.platform = platform; }
    }

    public static class RefreshRequest {
        @NotBlank
        private String refreshToken;

        // Getters and Setters
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }

    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private long expiresIn;
        private UserDto.Response user;

        // Constructors
        public TokenResponse() {}

        public TokenResponse(String accessToken, String refreshToken, String tokenType, long expiresIn) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.tokenType = tokenType;
            this.expiresIn = expiresIn;
        }

        // Getters and Setters
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }
        public long getExpiresIn() { return expiresIn; }
        public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
        public UserDto.Response getUser() { return user; }
        public void setUser(UserDto.Response user) { this.user = user; }
    }
}
