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
import com.ehocam.api_gateway.mapper.UserMapper;
import com.ehocam.api_gateway.service.AuthService;

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
    private UserMapper userMapper;

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
        UserDto.Response response = userMapper.toResponseDto(user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update current authenticated user's profile")
    public ResponseEntity<UserDto.Response> updateProfile(
            @Valid @RequestBody UserDto.UpdateRequest request) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = authService.updateUserProfile(username, request);
        UserDto.Response response = userMapper.toResponseDto(user);
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
}
