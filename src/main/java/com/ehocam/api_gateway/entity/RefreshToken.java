package com.ehocam.api_gateway.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_refresh_user_session", columnList = "user_id, session_id"),
    @Index(name = "idx_refresh_user_active", columnList = "user_id"),
    @Index(name = "idx_refresh_expires", columnList = "expires_at")
})
public class RefreshToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "session_id", nullable = false)
    private UUID sessionId;
    
    @Column(name = "device_id")
    private String deviceId;
    
    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;
    
    @Column(name = "issued_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime issuedAt;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "used", nullable = false)
    private Boolean used = false;
    
    @Column(name = "revoked", nullable = false)
    private Boolean revoked = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rotated_from_id")
    private RefreshToken rotatedFrom;
    
    @Column(name = "ip_created", columnDefinition = "inet")
    private String ipCreated;
    
    @Column(name = "ua_created")
    private String uaCreated;
    
    // Constructors
    public RefreshToken() {}
    
    public RefreshToken(User user, UUID sessionId, String deviceId, String tokenHash, 
                       LocalDateTime expiresAt, String ipCreated, String uaCreated) {
        this.user = user;
        this.sessionId = sessionId;
        this.deviceId = deviceId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.ipCreated = ipCreated;
        this.uaCreated = uaCreated;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }
    
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public Boolean getUsed() { return used; }
    public void setUsed(Boolean used) { this.used = used; }
    
    public Boolean getRevoked() { return revoked; }
    public void setRevoked(Boolean revoked) { this.revoked = revoked; }
    
    public RefreshToken getRotatedFrom() { return rotatedFrom; }
    public void setRotatedFrom(RefreshToken rotatedFrom) { this.rotatedFrom = rotatedFrom; }
    
    public String getIpCreated() { return ipCreated; }
    public void setIpCreated(String ipCreated) { this.ipCreated = ipCreated; }
    
    public String getUaCreated() { return uaCreated; }
    public void setUaCreated(String uaCreated) { this.uaCreated = uaCreated; }
    
    // Helper methods
    public boolean isActive() {
        return !used && !revoked && expiresAt.isAfter(LocalDateTime.now());
    }
    
    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }
    
    public void markAsUsed() {
        this.used = true;
    }
    
    public void markAsRevoked() {
        this.revoked = true;
    }
}
