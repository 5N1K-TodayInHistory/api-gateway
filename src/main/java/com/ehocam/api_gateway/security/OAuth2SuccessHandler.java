package com.ehocam.api_gateway.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.ehocam.api_gateway.entity.User;
import com.ehocam.api_gateway.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oauth2User.getUser();
        
        // Generate JWT tokens
        var tokenResponse = authService.generateTokensForUser(user);
        
        // Redirect to frontend with tokens
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/auth/callback")
                .queryParam("access_token", tokenResponse.getAccessToken())
                .queryParam("refresh_token", tokenResponse.getRefreshToken())
                .queryParam("token_type", tokenResponse.getTokenType())
                .queryParam("expires_in", tokenResponse.getExpiresIn())
                .build().toUriString();
        
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
