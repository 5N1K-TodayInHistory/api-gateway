package com.ehocam.api_gateway.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ehocam.api_gateway.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "User profile response data")
    public static class Response {
        @Schema(description = "User ID")
        private Long id;
        
        @Schema(description = "User email address")
        private String email;
        
        @Schema(description = "Authentication provider")
        private User.AuthProvider authProvider;
        
        
        @Schema(description = "Display name")
        private String displayName;
        
        @Schema(description = "Avatar URL")
        private String avatarUrl;
        
        @Schema(description = "User preferences")
        private UserPreferencesDto preferences;
        
        @Schema(description = "User devices")
        private List<DeviceDto> devices;
        
        @Schema(description = "Creation timestamp")
        private LocalDateTime createdAt;
        
        @Schema(description = "Last update timestamp")
        private LocalDateTime updatedAt;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public User.AuthProvider getAuthProvider() { return authProvider; }
        public void setAuthProvider(User.AuthProvider authProvider) { this.authProvider = authProvider; }
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

    @Schema(description = "User profile update request")
    public static class UpdateRequest {
        @Schema(description = "Display name", example = "John Doe")
        @NotBlank(message = "Display name is required")
        private String displayName;
        
        @Schema(description = "Avatar URL", example = "https://example.com/avatar.jpg")
        private String avatarUrl;
        
        @Schema(description = "User preferences")
        private UserPreferencesDto preferences;

        // Getters and Setters
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
        public UserPreferencesDto getPreferences() { return preferences; }
        public void setPreferences(UserPreferencesDto preferences) { this.preferences = preferences; }
    }

    @Schema(description = "User preferences configuration")
    public static class UserPreferencesDto {
        @Schema(description = "View mode preference", example = "list", allowableValues = {"list", "grid"}, required = false)
        private String viewMode = "list";
        
        @Schema(description = "Selected country code", example = "US", required = false)
        private String selectedCountry = "US";
        
        @Schema(description = "Selected event categories", example = "[\"science\", \"sports\", \"politics\"]", required = false)
        private List<String> selectedCategories = List.of("science", "sports");
        
        @Schema(description = "Language preference", example = "en", allowableValues = {"en", "tr", "es", "de", "fr", "ar"}, required = false)
        private String language = "en";
        
        @Schema(description = "Timezone preference", example = "UTC", required = false)
        private String timezone = "UTC";
        
        @Schema(description = "Push notifications enabled", example = "false", required = false)
        private boolean notifications = false;
        
        @Schema(description = "Dark mode enabled", example = "false", required = false)
        private boolean darkMode = false;

        // Getters and Setters
        public String getViewMode() { return viewMode; }
        public void setViewMode(String viewMode) { this.viewMode = viewMode; }
        public String getSelectedCountry() { return selectedCountry; }
        public void setSelectedCountry(String selectedCountry) { this.selectedCountry = selectedCountry; }
        public List<String> getSelectedCategories() { return selectedCategories; }
        public void setSelectedCategories(List<String> selectedCategories) { this.selectedCategories = selectedCategories; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }
        public boolean isNotifications() { return notifications; }
        public void setNotifications(boolean notifications) { this.notifications = notifications; }
        public boolean isDarkMode() { return darkMode; }
        public void setDarkMode(boolean darkMode) { this.darkMode = darkMode; }

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
