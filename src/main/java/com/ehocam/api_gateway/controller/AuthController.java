package com.ehocam.api_gateway.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ehocam.api_gateway.dto.AuthDto;
import com.ehocam.api_gateway.dto.UserDto;
import com.ehocam.api_gateway.entity.User;
import com.ehocam.api_gateway.service.AuthService;
import com.ehocam.api_gateway.service.GoogleOAuth2Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication endpoints for OAuth2 and JWT operations")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private GoogleOAuth2Service googleOAuth2Service;

    @PostMapping("/oauth/google")
    @Operation(summary = "Google OAuth2 login", description = "Authenticate with Google ID token and get JWT tokens")
    public ResponseEntity<AuthDto.TokenResponse> googleOAuth2Login(
            @Valid @RequestBody AuthDto.GoogleOAuthRequest request) {
        
        try {
            // Verify Google ID token and get/create user
            User user = googleOAuth2Service.verifyIdTokenAndGetUser(request.getIdToken());
            
            // Generate our own JWT tokens
            AuthDto.TokenResponse response = authService.generateTokensForUser(user);
            response.setUser(convertToUserResponse(user));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Google OAuth2 authentication failed: " + e.getMessage(), e);
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Exchange refresh token for new access token")
    public ResponseEntity<AuthDto.TokenResponse> refreshToken(
            @Valid @RequestBody AuthDto.RefreshRequest request) {
        
        AuthDto.TokenResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Get current authenticated user's profile")
    public ResponseEntity<UserDto.Response> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = authService.getUserByUsername(username);
        UserDto.Response response = convertToUserResponse(user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update current authenticated user's profile")
    public ResponseEntity<UserDto.Response> updateProfile(
            @Valid @RequestBody UserDto.UpdateRequest request) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = authService.updateUserProfile(username, request);
        UserDto.Response response = convertToUserResponse(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout current user (invalidate refresh token)")
    public ResponseEntity<Map<String, String>> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        authService.logout(username);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    private UserDto.Response convertToUserResponse(User user) {
        UserDto.Response response = new UserDto.Response();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setAuthProvider(user.getAuthProvider());
        response.setRoles(user.getRoles());
        response.setDisplayName(user.getDisplayName());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        // Convert preferences if exists
        if (user.getPreferences() != null) {
            UserDto.UserPreferencesDto preferencesDto = new UserDto.UserPreferencesDto();
            preferencesDto.setViewMode(user.getPreferences().getViewMode());
            preferencesDto.setCountries(user.getPreferences().getCountries());
            preferencesDto.setCategories(user.getPreferences().getCategories());
            preferencesDto.setLanguage(user.getPreferences().getLanguage());
            
            if (user.getPreferences().getNotifications() != null) {
                UserDto.UserPreferencesDto.NotificationPreferencesDto notificationDto = 
                    new UserDto.UserPreferencesDto.NotificationPreferencesDto();
                notificationDto.setDaily(user.getPreferences().getNotifications().isDaily());
                notificationDto.setBreaking(user.getPreferences().getNotifications().isBreaking());
                preferencesDto.setNotifications(notificationDto);
            }
            
            response.setPreferences(preferencesDto);
        }
        
        return response;
    }
}
