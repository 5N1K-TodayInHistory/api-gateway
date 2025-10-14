package com.ehocam.api_gateway.security;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.ehocam.api_gateway.entity.User;

public class CustomOAuth2User implements OAuth2User {
    
    private final OAuth2User oauth2User;
    private final User user;
    
    public CustomOAuth2User(OAuth2User oauth2User, User user) {
        this.oauth2User = oauth2User;
        this.user = user;
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .toList();
    }
    
    @Override
    public String getName() {
        return user.getUsername();
    }
    
    public User getUser() {
        return user;
    }
    
    public String getEmail() {
        return user.getEmail();
    }
    
    public String getDisplayName() {
        return user.getDisplayName();
    }
}
