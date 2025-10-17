package com.ehocam.api_gateway.controller;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ehocam.api_gateway.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health Check", description = "System health check endpoints for mobile app")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/status")
    @Operation(summary = "Check system health", description = "Check if backend services are running and accessible")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "System is healthy",
                       content = @Content(mediaType = "application/json", 
                                        schema = @Schema(implementation = HealthStatus.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "503", description = "System is unhealthy")
    public ResponseEntity<ApiResponse<HealthStatus>> checkHealth() {
        HealthStatus healthStatus = new HealthStatus();
        
        // Check database connection
        boolean dbHealthy = checkDatabaseHealth();
        healthStatus.setDatabase(dbHealthy);
        
        // Check overall system health
        boolean systemHealthy = dbHealthy; // Add more checks here if needed
        
        healthStatus.setStatus(systemHealthy ? "UP" : "DOWN");
        healthStatus.setTimestamp(LocalDateTime.now().toString());
        
        if (systemHealthy) {
            return ResponseEntity.ok(ApiResponse.success(healthStatus));
        } else {
            return ResponseEntity.status(503).body(ApiResponse.error("System is unhealthy", healthStatus));
        }
    }

    private boolean checkDatabaseHealth() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(5); // 5 second timeout
        } catch (Exception e) {
            return false;
        }
    }

    // Health status DTO
    @Schema(description = "System health status information")
    public static class HealthStatus {
        @Schema(description = "Overall system status", example = "UP", allowableValues = {"UP", "DOWN"})
        private String status;
        
        @Schema(description = "Health check timestamp", example = "2025-10-17T11:24:22.206566")
        private String timestamp;
        
        @Schema(description = "Database connection status", example = "true")
        private boolean database;
        
        @Schema(description = "Additional health details")
        private Map<String, Object> details = new HashMap<>();

        // Constructors
        public HealthStatus() {}

        // Getters and Setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        
        public boolean isDatabase() { return database; }
        public void setDatabase(boolean database) { this.database = database; }
        
        public Map<String, Object> getDetails() { return details; }
        public void setDetails(Map<String, Object> details) { this.details = details; }
    }
}
