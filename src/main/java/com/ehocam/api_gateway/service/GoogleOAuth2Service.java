package com.ehocam.api_gateway.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ehocam.api_gateway.config.GoogleOAuth2Config;
import com.ehocam.api_gateway.entity.User;
import com.ehocam.api_gateway.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

@Service
public class GoogleOAuth2Service {
    
    private static final Logger logger = LoggerFactory.getLogger(GoogleOAuth2Service.class);
    
    @Autowired
    private GoogleOAuth2Config googleOAuth2Config;
    
    @Autowired
    private UserRepository userRepository;
    
    public User verifyIdTokenAndGetUser(String idToken) throws GeneralSecurityException, IOException {
        // Validate input
        if (idToken == null || idToken.trim().isEmpty()) {
            throw new IllegalArgumentException("ID token is null or empty");
        }
        
        // Check if client ID is configured
        String clientId = googleOAuth2Config.getClientId();
        if (clientId == null || clientId.equals("your-google-client-id")) {
            logger.warn("Google Client ID is not configured. Using default for testing purposes.");
            clientId = "test-client-id"; // For testing only
        }
        
        // Check token format (basic JWT format check)
        String[] parts = idToken.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT format. Expected 3 parts separated by dots.");
        }
        
        logger.debug("Verifying Google ID token with client ID: {}", clientId);
        
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
        
        GoogleIdToken googleIdToken;
        try {
            googleIdToken = verifier.verify(idToken);
        } catch (Exception e) {
            logger.error("Failed to verify Google ID token: {}", e.getMessage());
            // Check if it's an expiration error
            if (e.getMessage().contains("expired") || e.getMessage().contains("exp")) {
                throw new IllegalArgumentException("Google ID token has expired. Please get a new token from Google.");
            }
            throw new IllegalArgumentException("Failed to verify Google ID token: " + e.getMessage());
        }
        
        if (googleIdToken == null) {
            throw new IllegalArgumentException("Invalid Google ID token - verification failed. Token may be expired or malformed.");
        }
        
        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        
        // Verify issuer
        if (!googleOAuth2Config.getIssuer().equals(payload.getIssuer())) {
            throw new IllegalArgumentException("Invalid issuer");
        }
        
        // Verify email is verified
        Boolean emailVerified = (Boolean) payload.get("email_verified");
        if (emailVerified == null || !emailVerified) {
            throw new IllegalArgumentException("Email not verified");
        }
        
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");
        String subject = payload.getSubject();
        
        logger.info("Google OAuth2 verification successful for user: {}", email);
        
        // Find or create user
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Update user info if needed
            if (name != null && !name.equals(user.getDisplayName())) {
                user.setDisplayName(name);
            }
            if (picture != null && !picture.equals(user.getAvatarUrl())) {
                user.setAvatarUrl(picture);
            }
            return userRepository.save(user);
        } else {
            // Create new user
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setDisplayName(name);
            newUser.setAvatarUrl(picture);
            newUser.setAuthProvider(User.AuthProvider.GOOGLE);
            newUser.setUsername(email); // Use email as username for Google users
            
            return userRepository.save(newUser);
        }
    }
}
