package com.ehocam.api_gateway.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.ehocam.api_gateway.dto.UserDto.UserPreferencesDto;
import com.ehocam.api_gateway.entity.User;

public class UserInfo {
    
    private Long id;
    private String email;
    private String username;
    private String displayName;
    private String avatarUrl;
    private User.AuthProvider authProvider;
    private Set<User.Role> roles;
    private UserPreferencesDto preferences;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public UserInfo() {}
    
    public UserInfo(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.avatarUrl = user.getAvatarUrl();
        this.authProvider = user.getAuthProvider();
        this.roles = user.getRoles();
        this.preferences = convertToUserPreferencesDto(user.getPreferences());
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
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
    
    public User.AuthProvider getAuthProvider() {
        return authProvider;
    }
    
    public void setAuthProvider(User.AuthProvider authProvider) {
        this.authProvider = authProvider;
    }
    
    public Set<User.Role> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<User.Role> roles) {
        this.roles = roles;
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
    
    public UserPreferencesDto getPreferences() {
        return preferences;
    }
    
    public void setPreferences(UserPreferencesDto preferences) {
        this.preferences = preferences;
    }
    
    // Helper method to convert User.UserPreferences to UserPreferencesDto
    private UserPreferencesDto convertToUserPreferencesDto(User.UserPreferences userPreferences) {
        if (userPreferences == null) {
            // Return default preferences if user has no preferences
            return new UserPreferencesDto();
        }
        
        UserPreferencesDto dto = new UserPreferencesDto();
        dto.setViewMode(userPreferences.getViewMode());
        
        // Convert countries list to selectedCountry (take first one)
        if (userPreferences.getCountries() != null && !userPreferences.getCountries().isEmpty()) {
            dto.setSelectedCountry(userPreferences.getCountries().get(0));
        }
        
        // Convert categories list to selectedCategories
        dto.setSelectedCategories(userPreferences.getCategories());
        dto.setLanguage(userPreferences.getLanguage());
        
        // Convert NotificationPreferences to boolean (combine daily and breaking)
        if (userPreferences.getNotifications() != null) {
            dto.setNotifications(userPreferences.getNotifications().isDaily() || userPreferences.getNotifications().isBreaking());
        }
        
        // Set default darkMode since it doesn't exist in entity
        dto.setDarkMode(true);
        
        return dto;
    }
}
