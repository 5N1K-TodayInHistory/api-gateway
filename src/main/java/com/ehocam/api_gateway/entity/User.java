package com.ehocam.api_gateway.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email", columnList = "email", unique = true),
    @Index(name = "idx_users_fcm_token", columnList = "devices")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(unique = true, nullable = true)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "auth_provider")
    private AuthProvider authProvider;

    @Column(name = "password_hash")
    private String passwordHash;


    @Column(name = "display_name")
    private String displayName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferences", columnDefinition = "jsonb")
    private UserPreferences preferences;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "devices", columnDefinition = "jsonb")
    private List<Device> devices;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public User() {}

    public User(String email, AuthProvider authProvider) {
        this.email = email;
        this.authProvider = authProvider;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public UserPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(UserPreferences preferences) {
        this.preferences = preferences;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Enums
    public enum AuthProvider {
        LOCAL, GOOGLE, MSFT, APPLE, GUEST
    }


    // Nested classes for JSONB
    public static class UserPreferences {
        private String viewMode = "GRID";
        private List<String> countries = List.of("TR");
        private List<String> categories = List.of("science", "politics", "sports", "history", "entertainment");
        private String language = "en";
        private String timezone = "UTC"; // Default timezone
        private NotificationPreferences notifications = new NotificationPreferences();

        // Getters and Setters
        public String getViewMode() { return viewMode; }
        public void setViewMode(String viewMode) { this.viewMode = viewMode; }
        public List<String> getCountries() { return countries; }
        public void setCountries(List<String> countries) { this.countries = countries; }
        public List<String> getCategories() { return categories; }
        public void setCategories(List<String> categories) { this.categories = categories; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }
        public NotificationPreferences getNotifications() { return notifications; }
        public void setNotifications(NotificationPreferences notifications) { this.notifications = notifications; }

        public static class NotificationPreferences {
            private boolean daily = true;
            private boolean breaking = true;

            public boolean isDaily() { return daily; }
            public void setDaily(boolean daily) { this.daily = daily; }
            public boolean isBreaking() { return breaking; }
            public void setBreaking(boolean breaking) { this.breaking = breaking; }
        }
    }

    public static class Device {
        private String fcmToken;
        private String platform;
        private LocalDateTime lastSeen;

        public Device() {}

        public Device(String fcmToken, String platform) {
            this.fcmToken = fcmToken;
            this.platform = platform;
            this.lastSeen = LocalDateTime.now();
        }

        // Getters and Setters
        public String getFcmToken() { return fcmToken; }
        public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }
        public String getPlatform() { return platform; }
        public void setPlatform(String platform) { this.platform = platform; }
        public LocalDateTime getLastSeen() { return lastSeen; }
        public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }
    }
}
