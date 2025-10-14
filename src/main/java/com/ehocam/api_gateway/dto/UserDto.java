package com.ehocam.api_gateway.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.ehocam.api_gateway.entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserDto {

    public static class Create {
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;
        
        @NotNull(message = "Auth provider is required")
        private User.AuthProvider authProvider;
        
        private String password;
        
        @NotBlank(message = "Display name is required")
        private String displayName;
        
        private String avatarUrl;
        
        private UserPreferencesDto preferences;
        
        private List<DeviceDto> devices;

        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public User.AuthProvider getAuthProvider() { return authProvider; }
        public void setAuthProvider(User.AuthProvider authProvider) { this.authProvider = authProvider; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
        public UserPreferencesDto getPreferences() { return preferences; }
        public void setPreferences(UserPreferencesDto preferences) { this.preferences = preferences; }
        public List<DeviceDto> getDevices() { return devices; }
        public void setDevices(List<DeviceDto> devices) { this.devices = devices; }
    }

    public static class Update {
        private String displayName;
        private String avatarUrl;
        private UserPreferencesDto preferences;

        // Getters and Setters
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
        public UserPreferencesDto getPreferences() { return preferences; }
        public void setPreferences(UserPreferencesDto preferences) { this.preferences = preferences; }
    }

    public static class Response {
        private Long id;
        private String email;
        private User.AuthProvider authProvider;
        private Set<User.Role> roles;
        private String displayName;
        private String avatarUrl;
        private UserPreferencesDto preferences;
        private List<DeviceDto> devices;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public User.AuthProvider getAuthProvider() { return authProvider; }
        public void setAuthProvider(User.AuthProvider authProvider) { this.authProvider = authProvider; }
        public Set<User.Role> getRoles() { return roles; }
        public void setRoles(Set<User.Role> roles) { this.roles = roles; }
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
        public UserPreferencesDto getPreferences() { return preferences; }
        public void setPreferences(UserPreferencesDto preferences) { this.preferences = preferences; }
        public List<DeviceDto> getDevices() { return devices; }
        public void setDevices(List<DeviceDto> devices) { this.devices = devices; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class UpdateRequest {
        @NotBlank(message = "Display name is required")
        private String displayName;
        private String avatarUrl;
        private UserPreferencesDto preferences;

        // Getters and Setters
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
        public UserPreferencesDto getPreferences() { return preferences; }
        public void setPreferences(UserPreferencesDto preferences) { this.preferences = preferences; }
    }

    public static class UserPreferencesDto {
        private String viewMode = "GRID";
        private List<String> countries = List.of("TR");
        private List<String> categories = List.of("science", "politics", "sports", "history", "entertainment");
        private String language = "en";
        private NotificationPreferencesDto notifications = new NotificationPreferencesDto();

        // Getters and Setters
        public String getViewMode() { return viewMode; }
        public void setViewMode(String viewMode) { this.viewMode = viewMode; }
        public List<String> getCountries() { return countries; }
        public void setCountries(List<String> countries) { this.countries = countries; }
        public List<String> getCategories() { return categories; }
        public void setCategories(List<String> categories) { this.categories = categories; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        public NotificationPreferencesDto getNotifications() { return notifications; }
        public void setNotifications(NotificationPreferencesDto notifications) { this.notifications = notifications; }

        public static class NotificationPreferencesDto {
            private boolean daily = true;
            private boolean breaking = true;

            public boolean isDaily() { return daily; }
            public void setDaily(boolean daily) { this.daily = daily; }
            public boolean isBreaking() { return breaking; }
            public void setBreaking(boolean breaking) { this.breaking = breaking; }
        }
    }

    public static class DeviceDto {
        private String fcmToken;
        private String platform;
        private LocalDateTime lastSeen;

        // Getters and Setters
        public String getFcmToken() { return fcmToken; }
        public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }
        public String getPlatform() { return platform; }
        public void setPlatform(String platform) { this.platform = platform; }
        public LocalDateTime getLastSeen() { return lastSeen; }
        public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }
    }
}
