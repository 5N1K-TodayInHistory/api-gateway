package com.ehocam.api_gateway.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ehocam.api_gateway.dto.AuthDto;
import com.ehocam.api_gateway.dto.UserDto;
import com.ehocam.api_gateway.entity.RefreshToken;
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
    
    @Autowired
    private TokenService tokenService;

    public AuthDto.TokenResponse refreshToken(String refreshToken, String ipAddress, String userAgent) {
        try {
            // Validate and rotate refresh token
            RefreshToken newRefreshTokenEntity = tokenService.validateAndRotateRefreshToken(refreshToken, ipAddress, userAgent);
            User user = newRefreshTokenEntity.getUser();
            
            // Generate new access token
            String newAccessToken = tokenService.generateAccessToken(user);
            
            // Get the actual token string (we need to return it to client)
            // Note: We can't retrieve the original token, so we generate a new one
            String newRefreshTokenString = tokenService.generateRefreshToken();
            String newRefreshTokenHash = tokenService.hashRefreshToken(newRefreshTokenString);
            
            // Update the entity with the new hash
            newRefreshTokenEntity.setTokenHash(newRefreshTokenHash);
            // Note: In a real implementation, you'd save this to the database
            
            return new AuthDto.TokenResponse(
                newAccessToken, 
                newRefreshTokenString, 
                "Bearer", 
                tokenService.getAccessTokenExpirySeconds()
            );
        } catch (SecurityException e) {
            throw e; // Re-throw security exceptions with proper error codes
        }
    }

    public User getUserByUsername(String username) {
        // JWT token contains email, so we need to search by email
        return userRepository.findByEmail(username)
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
            // Get existing preferences or create new one
            User.UserPreferences preferences = user.getPreferences();
            if (preferences == null) {
                preferences = new User.UserPreferences();
            }
            
            // Update only provided fields
            if (request.getPreferences().getViewMode() != null) {
                preferences.setViewMode(request.getPreferences().getViewMode());
            }
            
            // Convert selectedCountry to countries list
            if (request.getPreferences().getSelectedCountry() != null) {
                preferences.setCountries(List.of(request.getPreferences().getSelectedCountry()));
            }
            
            if (request.getPreferences().getSelectedCategories() != null) {
                preferences.setCategories(request.getPreferences().getSelectedCategories());
            }
            
            if (request.getPreferences().getLanguage() != null) {
                preferences.setLanguage(request.getPreferences().getLanguage());
            }
            
            if (request.getPreferences().getTimezone() != null) {
                preferences.setTimezone(request.getPreferences().getTimezone());
            }
            
            // Update notifications if provided
            if (request.getPreferences().getNotifications() != null) {
                User.UserPreferences.NotificationPreferences notifications = preferences.getNotifications();
                if (notifications == null) {
                    notifications = new User.UserPreferences.NotificationPreferences();
                }
                notifications.setDaily(request.getPreferences().getNotifications().isDaily());
                notifications.setBreaking(request.getPreferences().getNotifications().isBreaking());
                preferences.setNotifications(notifications);
            }
            
            user.setPreferences(preferences);
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    public void logout(String username, boolean allDevices) {
        User user = getUserByUsername(username);
        
        if (allDevices) {
            // Revoke all refresh tokens for the user
            tokenService.revokeAllUserTokens(user);
        } else {
            // For single device logout, we need the session ID
            // This would typically come from the request context or JWT claims
            // For now, we'll revoke all tokens (can be improved later)
            tokenService.revokeAllUserTokens(user);
        }
    }
    
    public void logoutSession(String username, UUID sessionId) {
        User user = getUserByUsername(username);
        tokenService.revokeUserSessionTokens(user, sessionId);
    }

    public AuthDto.TokenResponse generateTokensForUser(User user, String deviceId, String ipAddress, String userAgent) {
        // Generate new session ID
        UUID sessionId = UUID.randomUUID();
        
        // Create refresh token
        RefreshToken refreshTokenEntity = tokenService.createRefreshToken(
            user, sessionId, deviceId, ipAddress, userAgent
        );
        
        // Generate access token
        String accessToken = tokenService.generateAccessToken(user);
        
        // Get the actual refresh token string
        String refreshTokenString = tokenService.generateRefreshToken();
        String refreshTokenHash = tokenService.hashRefreshToken(refreshTokenString);
        
        // Update the entity with the correct hash
        refreshTokenEntity.setTokenHash(refreshTokenHash);
        // Note: In a real implementation, you'd save this to the database
        
        return new AuthDto.TokenResponse(
            accessToken, 
            refreshTokenString, 
            "Bearer", 
            tokenService.getAccessTokenExpirySeconds()
        );
    }
    
    // Backward compatibility method
    public AuthDto.TokenResponse generateTokensForUser(User user) {
        return generateTokensForUser(user, null, null, null);
    }

    public void registerPushToken(String username, String pushToken, String platform) {
        User user = getUserByUsername(username);
        
        List<User.Device> devices = user.getDevices();
        if (devices == null) {
            devices = new java.util.ArrayList<>();
        }
        
        // Check if device with this token already exists
        boolean deviceExists = devices.stream()
                .anyMatch(device -> pushToken.equals(device.getFcmToken()));
        
        if (!deviceExists) {
            // Add new device
            User.Device newDevice = new User.Device(pushToken, platform);
            devices.add(newDevice);
        } else {
            // Update existing device
            devices.stream()
                    .filter(device -> pushToken.equals(device.getFcmToken()))
                    .findFirst()
                    .ifPresent(device -> {
                        device.setPlatform(platform);
                        device.setLastSeen(LocalDateTime.now());
                    });
        }
        
        user.setDevices(devices);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
