package com.ehocam.api_gateway.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ehocam.api_gateway.entity.RefreshToken;
import com.ehocam.api_gateway.entity.User;
import com.ehocam.api_gateway.repository.RefreshTokenRepository;
import com.ehocam.api_gateway.security.JwtUtil;

@Service
@Transactional
public class TokenService {
    
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
    private static final int REFRESH_TOKEN_BYTES = 32; // 256 bits
    private static final int ACCESS_TOKEN_EXPIRY_MINUTES = 15000;
    private static final int REFRESH_TOKEN_EXPIRY_DAYS = 30;
    private static final int MAX_REFRESH_TOKEN_AGE_DAYS = 90;
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * Generate a new opaque refresh token
     */
    public String generateRefreshToken() {
        byte[] randomBytes = new byte[REFRESH_TOKEN_BYTES];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
    
    /**
     * Hash a refresh token using SHA-256
     */
    public String hashRefreshToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    /**
     * Create and save a new refresh token
     */
    public RefreshToken createRefreshToken(User user, UUID sessionId, String deviceId, 
                                         String ipAddress, String userAgent) {
        String token = generateRefreshToken();
        String tokenHash = hashRefreshToken(token);
        
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRY_DAYS);
        
        RefreshToken refreshToken = new RefreshToken(
            user, sessionId, deviceId, tokenHash, expiresAt, ipAddress, userAgent
        );
        
        return refreshTokenRepository.save(refreshToken);
    }
    
    /**
     * Validate and rotate refresh token
     */
    public RefreshToken validateAndRotateRefreshToken(String token, String ipAddress, String userAgent) {
        String tokenHash = hashRefreshToken(token);
        
        // Find the token (including used/revoked ones for reuse detection)
        RefreshToken existingToken = refreshTokenRepository.findByTokenHash(tokenHash)
            .orElseThrow(() -> new SecurityException("refresh_invalid"));
        
        // Check if token is already used (reuse detection)
        if (existingToken.getUsed()) {
            logger.warn("Refresh token reuse detected for user: {}, session: {}", 
                       existingToken.getUser().getId(), existingToken.getSessionId());
            
            // Revoke all tokens in this session
            refreshTokenRepository.revokeAllInSession(existingToken.getUser(), existingToken.getSessionId());
            throw new SecurityException("refresh_reused");
        }
        
        // Check if token is revoked
        if (existingToken.getRevoked()) {
            throw new SecurityException("refresh_revoked");
        }
        
        // Check if token is expired
        if (existingToken.isExpired()) {
            throw new SecurityException("refresh_expired");
        }
        
        // Check if token is too old (hard expiry)
        LocalDateTime maxAge = existingToken.getIssuedAt().plusDays(MAX_REFRESH_TOKEN_AGE_DAYS);
        if (LocalDateTime.now().isAfter(maxAge)) {
            throw new SecurityException("refresh_expired");
        }
        
        // Mark old token as used and revoked
        existingToken.markAsUsed();
        existingToken.markAsRevoked();
        refreshTokenRepository.save(existingToken);
        
        // Create new refresh token
        String newToken = generateRefreshToken();
        String newTokenHash = hashRefreshToken(newToken);
        LocalDateTime newExpiresAt = LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRY_DAYS);
        
        RefreshToken newRefreshToken = new RefreshToken(
            existingToken.getUser(), 
            existingToken.getSessionId(), 
            existingToken.getDeviceId(), 
            newTokenHash, 
            newExpiresAt, 
            ipAddress, 
            userAgent
        );
        newRefreshToken.setRotatedFrom(existingToken);
        
        return refreshTokenRepository.save(newRefreshToken);
    }
    
    /**
     * Generate access token for user
     */
    public String generateAccessToken(User user) {
        return jwtUtil.generateAccessToken(user.getUsername());
    }
    
    /**
     * Revoke all refresh tokens for a user
     */
    public void revokeAllUserTokens(User user) {
        int revokedCount = refreshTokenRepository.revokeAllByUser(user);
        logger.info("Revoked {} refresh tokens for user: {}", revokedCount, user.getId());
    }
    
    /**
     * Revoke all refresh tokens for a user and session
     */
    public void revokeUserSessionTokens(User user, UUID sessionId) {
        int revokedCount = refreshTokenRepository.revokeAllByUserAndSession(user, sessionId);
        logger.info("Revoked {} refresh tokens for user: {}, session: {}", revokedCount, user.getId(), sessionId);
    }
    
    /**
     * Get active sessions for a user
     */
    public List<UUID> getActiveSessions(User user) {
        return refreshTokenRepository.findActiveSessionsByUser(user, LocalDateTime.now());
    }
    
    /**
     * Clean up expired tokens
     */
    public int cleanupExpiredTokens() {
        return refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
    
    /**
     * Get access token expiry time in seconds
     */
    public long getAccessTokenExpirySeconds() {
        return ACCESS_TOKEN_EXPIRY_MINUTES * 60L;
    }
    
    /**
     * Get refresh token expiry time in seconds
     */
    public long getRefreshTokenExpirySeconds() {
        return REFRESH_TOKEN_EXPIRY_DAYS * 24L * 60L * 60L;
    }
}
