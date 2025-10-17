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

import com.ehocam.api_gateway.dto.ApiResponse;
import com.ehocam.api_gateway.dto.AuthDto;
import com.ehocam.api_gateway.dto.AuthResponse;
import com.ehocam.api_gateway.dto.AuthTokens;
import com.ehocam.api_gateway.dto.UserDto;
import com.ehocam.api_gateway.dto.UserInfo;
import com.ehocam.api_gateway.entity.User;
import com.ehocam.api_gateway.security.JwtUtil;
import com.ehocam.api_gateway.service.AuthService;
import com.ehocam.api_gateway.service.GoogleOAuth2Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication endpoints for OAuth2 and JWT operations")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private GoogleOAuth2Service googleOAuth2Service;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/oauth/google")
    @Operation(summary = "Google OAuth2 login", description = "Authenticate with Google ID token and get JWT tokens")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully authenticated",
                       content = @Content(mediaType = "application/json", 
                                        schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<ApiResponse<AuthResponse>> googleOAuth2Login(
            @Valid @RequestBody AuthDto.GoogleOAuthRequest request,
            HttpServletRequest httpRequest) {

        try {
            // Validate request structure
            if (request.getIdToken() == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid Google OAuth request: idToken is required"));
            }

            // Verify Google ID token and get/create user
            User user = googleOAuth2Service.verifyIdTokenAndGetUser(request.getIdToken());

            // Get client information
            String ipAddress = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");
            String deviceId = request.getId(); // Use Google user ID as device identifier

            // Generate our own JWT tokens with session management
            AuthDto.TokenResponse tokenResponse = authService.generateTokensForUser(
                    user, deviceId, ipAddress, userAgent
            );

            // Create AuthTokens
            AuthTokens tokens = new AuthTokens(
                    tokenResponse.getAccessToken(),
                    tokenResponse.getRefreshToken(),
                    tokenResponse.getTokenType(),
                    tokenResponse.getExpiresIn()
            );

            // Create UserInfo
            UserInfo userInfo = new UserInfo(user);

            // Create AuthResponse
            AuthResponse authResponse = new AuthResponse(tokens, userInfo);

            return ResponseEntity.ok(ApiResponse.success(authResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Google OAuth2 authentication failed: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Exchange refresh token for new access token with rotation")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully refreshed token",
                       content = @Content(mediaType = "application/json", 
                                        schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden")
    public ResponseEntity<ApiResponse<AuthTokens>> refreshToken(
            @Valid @RequestBody AuthDto.RefreshRequest request,
            HttpServletRequest httpRequest) {

        try {
            String ipAddress = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");

            AuthDto.TokenResponse tokenResponse = authService.refreshToken(
                    request.getRefreshToken(), ipAddress, userAgent
            );

            // Create AuthTokens
            AuthTokens tokens = new AuthTokens(
                    tokenResponse.getAccessToken(),
                    tokenResponse.getRefreshToken(),
                    tokenResponse.getTokenType(),
                    tokenResponse.getExpiresIn()
            );

            return ResponseEntity.ok(ApiResponse.success(tokens));
        } catch (SecurityException e) {
            // Return appropriate HTTP status based on error type
            switch (e.getMessage()) {
                case "refresh_invalid":
                    return ResponseEntity.status(401)
                            .body(ApiResponse.error("Invalid refresh token"));
                case "refresh_expired":
                    return ResponseEntity.status(401)
                            .body(ApiResponse.error("Refresh token has expired"));
                case "refresh_reused":
                    return ResponseEntity.status(401)
                            .body(ApiResponse.error("Refresh token has been reused - security violation"));
                case "refresh_revoked":
                    return ResponseEntity.status(403)
                            .body(ApiResponse.error("Refresh token has been revoked"));
                default:
                    break;
            }
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Token refresh failed"));
        }
    }

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Get current authenticated user's profile")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved user profile",
                       content = @Content(mediaType = "application/json", 
                                        schema = @Schema(implementation = UserDto.Response.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<ApiResponse<UserDto.Response>> getProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User user = authService.getUserByUsername(username);
            return ResponseEntity.ok(ApiResponse.success(convertToUserResponse(user)));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update current authenticated user's profile")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully updated user profile",
                       content = @Content(mediaType = "application/json", 
                                        schema = @Schema(implementation = UserDto.Response.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request")
    public ResponseEntity<ApiResponse<UserDto.Response>> updateProfile(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "User profile update data",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.UpdateRequest.class)
                )
            )
            @Valid @RequestBody UserDto.UpdateRequest request) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User user = authService.updateUserProfile(username, request);
            UserDto.Response response = convertToUserResponse(user);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout current user (invalidate refresh token)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully logged out",
                       content = @Content(mediaType = "application/json", 
                                        schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestBody(required = false) Map<String, Boolean> request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        boolean allDevices = request != null && request.getOrDefault("allDevices", false);
        authService.logout(username, allDevices);

        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    private UserDto.Response convertToUserResponse(User user) {
        UserDto.Response response = new UserDto.Response();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setAuthProvider(user.getAuthProvider());
        response.setDisplayName(user.getDisplayName());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        // Convert preferences if exists
        if (user.getPreferences() != null) {
            UserDto.UserPreferencesDto preferencesDto = new UserDto.UserPreferencesDto();
            preferencesDto.setViewMode(user.getPreferences().getViewMode());

            // Convert countries list to selectedCountry (take first one)
            if (user.getPreferences().getCountries() != null && !user.getPreferences().getCountries().isEmpty()) {
                preferencesDto.setSelectedCountry(user.getPreferences().getCountries().get(0));
            }

            preferencesDto.setSelectedCategories(user.getPreferences().getCategories());
            preferencesDto.setLanguage(user.getPreferences().getLanguage());
            preferencesDto.setTimezone(user.getPreferences().getTimezone());

            // Convert NotificationPreferences to boolean
            if (user.getPreferences().getNotifications() != null) {
                preferencesDto.setNotifications(user.getPreferences().getNotifications().isDaily() ||
                        user.getPreferences().getNotifications().isBreaking());
            }

            // Set default darkMode
            preferencesDto.setDarkMode(false);

            response.setPreferences(preferencesDto);
        }

        return response;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate token", description = "Check if the provided Bearer token is valid with database cross-check")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token validation result",
                       content = @Content(mediaType = "application/json", 
                                        schema = @Schema(implementation = Map.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<Map<String, Object>> validateToken(HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401)
                    .body(Map.of(
                        "success", false,
                        "valid", false,
                        "error", "No valid Bearer token provided"
                    ));
            }
            
            String token = authorizationHeader.substring(7);
            
            // Step 1: Validate JWT token format and signature
            try {
                String username = jwtUtil.getUsernameFromToken(token);
                boolean isJwtValid = !jwtUtil.isTokenExpired(token);
                
                if (!isJwtValid) {
                    return ResponseEntity.status(401)
                        .body(Map.of(
                            "success", false,
                            "valid", false,
                            "error", "Token has expired"
                        ));
                }
                
                // Step 2: Cross-check with database - verify user exists and is active
                User user = authService.getUserByUsername(username);
                if (user == null) {
                    return ResponseEntity.status(401)
                        .body(Map.of(
                            "success", false,
                            "valid", false,
                            "error", "User not found in database"
                        ));
                }
                
                // Step 3: Check if user account is active
                if (user.getIsActive() == null || !user.getIsActive()) {
                    return ResponseEntity.status(401)
                        .body(Map.of(
                            "success", false,
                            "valid", false,
                            "error", "User account is deactivated"
                        ));
                }
                
                // Step 4: Optional - Check if token is in our refresh token blacklist
                // This would require storing access tokens in Redis/DB for revocation
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "valid", true
                ));
                
            } catch (Exception e) {
                return ResponseEntity.status(401)
                    .body(Map.of(
                        "success", false,
                        "valid", false,
                        "error", "Invalid token format or signature"
                    ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "success", false,
                    "valid", false,
                    "error", "Internal server error: " + e.getMessage()
                ));
        }
    }
}
