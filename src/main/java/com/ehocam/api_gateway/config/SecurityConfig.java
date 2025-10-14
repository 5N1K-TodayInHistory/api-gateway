package com.ehocam.api_gateway.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ehocam.api_gateway.security.CustomUserDetailsService;
import com.ehocam.api_gateway.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/events").permitAll()
                .requestMatchers("/api/events/{id}").permitAll()
                .requestMatchers("/api/categories").permitAll()
                .requestMatchers("/api/countries").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/api-docs/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/error").permitAll()
                
                // Admin endpoints
                .requestMatchers("/api/events", "POST").hasRole("ADMIN")
                .requestMatchers("/api/events/{id}", "PUT").hasRole("ADMIN")
                .requestMatchers("/api/events/{id}", "DELETE").hasRole("ADMIN")
                
                // Authenticated endpoints
                .requestMatchers("/api/events/{id}/like").authenticated()
                .requestMatchers("/api/events/{id}/comments").authenticated()
                .requestMatchers("/api/events/{id}/share").authenticated()
                .requestMatchers("/api/user/**").authenticated()
                .requestMatchers("/api/ws/**").authenticated()
                
                // All other requests need authentication
                .anyRequest().authenticated()
            )
            // OAuth2 login disabled for now - will be enabled when OAuth2 controllers are implemented
            // .oauth2Login(oauth2 -> oauth2
            //     .loginPage("/api/auth/oauth2/login")
            //     .defaultSuccessUrl("/api/auth/oauth2/success", true)
            //     .failureUrl("/api/auth/oauth2/failure")
            //     .userInfoEndpoint(userInfo -> userInfo
            //         .userService(oauth2UserService())
            //     )
            // )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        // This will be implemented when we add OAuth2 user creation logic
        return new CustomOAuth2UserService();
    }

    // Placeholder OAuth2UserService - will be implemented later
    private static class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
        @Override
        public OAuth2User loadUser(OAuth2UserRequest userRequest) {
            // TODO: Implement OAuth2 user loading and creation
            throw new UnsupportedOperationException("OAuth2 user service not implemented yet");
        }
    }
}
