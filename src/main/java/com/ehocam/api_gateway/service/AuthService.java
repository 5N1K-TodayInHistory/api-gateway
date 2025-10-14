package com.ehocam.api_gateway.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ehocam.api_gateway.dto.AuthDto;
import com.ehocam.api_gateway.dto.UserDto;
import com.ehocam.api_gateway.entity.User;
import com.ehocam.api_gateway.repository.UserRepository;
import com.ehocam.api_gateway.security.JwtUtil;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthDto.TokenResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        String username = jwtUtil.getUsernameFromToken(refreshToken);
        User user = getUserByUsername(username);
        
        String newAccessToken = jwtUtil.generateAccessToken(user.getUsername());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        
        return new AuthDto.TokenResponse(newAccessToken, newRefreshToken, "Bearer", 3600);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public User updateUserProfile(String username, UserDto.UpdateRequest request) {
        User user = getUserByUsername(username);
        
        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        
        if (request.getPreferences() != null) {
            // Convert DTO to Entity preferences
            User.UserPreferences preferences = new User.UserPreferences();
            preferences.setViewMode(request.getPreferences().getViewMode());
            preferences.setCountries(request.getPreferences().getCountries());
            preferences.setCategories(request.getPreferences().getCategories());
            preferences.setLanguage(request.getPreferences().getLanguage());
            
            User.UserPreferences.NotificationPreferences notifications = new User.UserPreferences.NotificationPreferences();
            notifications.setDaily(request.getPreferences().getNotifications().isDaily());
            notifications.setBreaking(request.getPreferences().getNotifications().isBreaking());
            preferences.setNotifications(notifications);
            
            user.setPreferences(preferences);
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    public void logout(String username) {
        // TODO: Implement token blacklisting or refresh token invalidation
        // For now, just log the logout
        System.out.println("User logged out: " + username);
    }

    public AuthDto.TokenResponse generateTokensForUser(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        
        return new AuthDto.TokenResponse(accessToken, refreshToken, "Bearer", 3600);
    }
}
