package com.ehocam.api_gateway.security;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ehocam.api_gateway.entity.User;
import com.ehocam.api_gateway.repository.UserRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oauth2User.getAttributes();
        
        User user = processOAuth2User(provider, attributes);
        
        return new CustomOAuth2User(oauth2User, user);
    }

    private User processOAuth2User(String provider, Map<String, Object> attributes) {
        String email = getEmailFromAttributes(provider, attributes);
        String name = getNameFromAttributes(provider, attributes);
        String picture = getPictureFromAttributes(provider, attributes);
        
        if (email == null || email.trim().isEmpty()) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }
        
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Update auth provider if different
            User.AuthProvider authProvider = User.AuthProvider.valueOf(provider.toUpperCase());
            if (!user.getAuthProvider().equals(authProvider)) {
                user.setAuthProvider(authProvider);
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
            }
            return user;
        }
        
        // Create new user
        return createNewUser(provider, email, name, picture);
    }

    private User createNewUser(String provider, String email, String name, String picture) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(email); // Use email as username for OAuth2 users
        user.setAuthProvider(User.AuthProvider.valueOf(provider.toUpperCase()));
        user.setDisplayName(name);
        user.setAvatarUrl(picture);
        user.setRoles(Set.of(User.Role.USER));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        // Set default preferences
        User.UserPreferences preferences = new User.UserPreferences();
        preferences.setViewMode("GRID");
        preferences.setCountries(List.of("TR", "US"));
        preferences.setCategories(List.of("science", "politics", "sports"));
        preferences.setLanguage("en");
        
        User.UserPreferences.NotificationPreferences notifications = new User.UserPreferences.NotificationPreferences();
        notifications.setDaily(true);
        notifications.setBreaking(true);
        preferences.setNotifications(notifications);
        
        user.setPreferences(preferences);
        
        return userRepository.save(user);
    }

    private String getEmailFromAttributes(String provider, Map<String, Object> attributes) {
        switch (provider.toLowerCase()) {
            case "google":
                return (String) attributes.get("email");
            case "microsoft":
                return (String) attributes.get("mail");
            case "apple":
                return (String) attributes.get("email");
            default:
                return (String) attributes.get("email");
        }
    }

    private String getNameFromAttributes(String provider, Map<String, Object> attributes) {
        switch (provider.toLowerCase()) {
            case "google":
                return (String) attributes.get("name");
            case "microsoft":
                return (String) attributes.get("displayName");
            case "apple":
                return (String) attributes.get("name");
            default:
                return (String) attributes.get("name");
        }
    }

    private String getPictureFromAttributes(String provider, Map<String, Object> attributes) {
        switch (provider.toLowerCase()) {
            case "google":
                return (String) attributes.get("picture");
            case "microsoft":
                return null; // Microsoft doesn't provide profile picture in basic scope
            case "apple":
                return null; // Apple doesn't provide profile picture in basic scope
            default:
                return (String) attributes.get("picture");
        }
    }
}
