package com.ehocam.api_gateway.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ehocam.api_gateway.entity.RefreshToken;
import com.ehocam.api_gateway.entity.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    /**
     * Find active refresh token by hash
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.tokenHash = :tokenHash AND rt.revoked = false AND rt.used = false AND rt.expiresAt > :now")
    Optional<RefreshToken> findActiveByTokenHash(@Param("tokenHash") String tokenHash, @Param("now") LocalDateTime now);
    
    /**
     * Find refresh token by hash (including used/revoked ones for reuse detection)
     */
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    
    /**
     * Find all active refresh tokens for a user
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = :user AND rt.revoked = false AND rt.used = false AND rt.expiresAt > :now")
    List<RefreshToken> findActiveByUser(@Param("user") User user, @Param("now") LocalDateTime now);
    
    /**
     * Find all active refresh tokens for a user and session
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = :user AND rt.sessionId = :sessionId AND rt.revoked = false AND rt.used = false AND rt.expiresAt > :now")
    List<RefreshToken> findActiveByUserAndSession(@Param("user") User user, @Param("sessionId") UUID sessionId, @Param("now") LocalDateTime now);
    
    /**
     * Find all refresh tokens for a user and session (including used/revoked)
     */
    List<RefreshToken> findByUserAndSessionId(User user, UUID sessionId);
    
    /**
     * Revoke all active refresh tokens for a user
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user AND rt.revoked = false")
    int revokeAllByUser(@Param("user") User user);
    
    /**
     * Revoke all active refresh tokens for a user and session
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user AND rt.sessionId = :sessionId AND rt.revoked = false")
    int revokeAllByUserAndSession(@Param("user") User user, @Param("sessionId") UUID sessionId);
    
    /**
     * Revoke all refresh tokens in a session (for reuse detection)
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user AND rt.sessionId = :sessionId")
    int revokeAllInSession(@Param("user") User user, @Param("sessionId") UUID sessionId);
    
    /**
     * Clean up expired tokens
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);
    
    /**
     * Find all sessions for a user
     */
    @Query("SELECT DISTINCT rt.sessionId FROM RefreshToken rt WHERE rt.user = :user AND rt.revoked = false AND rt.used = false AND rt.expiresAt > :now")
    List<UUID> findActiveSessionsByUser(@Param("user") User user, @Param("now") LocalDateTime now);
}
