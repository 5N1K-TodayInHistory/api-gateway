package com.ehocam.api_gateway.service;

import com.ehocam.api_gateway.config.GoogleOAuth2Config;
import com.ehocam.api_gateway.entity.User;
import com.ehocam.api_gateway.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

@Service
public class GoogleOAuth2Service {
    
    private static final Logger logger = LoggerFactory.getLogger(GoogleOAuth2Service.class);
    
    @Autowired
    private GoogleOAuth2Config googleOAuth2Config;
    
    @Autowired
    private UserRepository userRepository;
    
    public User verifyIdTokenAndGetUser(String idToken) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleOAuth2Config.getClientId()))
                .build();
        
        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken == null) {
            throw new IllegalArgumentException("Invalid ID token");
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
