package com.ehocam.api_gateway.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ehocam.api_gateway.dto.ApiResponse;
import com.ehocam.api_gateway.dto.EventDto;
import com.ehocam.api_gateway.service.EventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Event management endpoints with i18n support")
public class EventController {

    @Autowired
    private EventService eventService;

    public Page<EventDto.Response> getEvents(LocalDateTime date,int page, int size){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            Page<EventDto.Response> events = eventService.getEventsForUser(username, page, size);
            return events;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch events: " + e.getMessage());
        }
    }

    @GetMapping("/today")
    @Operation(summary = "Get today's events", 
               description = "Get events for today based on user's language preference")
    public ResponseEntity<ApiResponse<Page<EventDto.Response>>> getTodayEvents(
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            // Get today's date
            LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            
            Page<EventDto.Response> events = getEvents(today, page, size);
            return ResponseEntity.ok(ApiResponse.success(events));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to fetch today's events: " + e.getMessage()));
        }
    }

    @GetMapping("/yesterday")
    @Operation(summary = "Get yesterday's events", 
               description = "Get events for yesterday based on user's language preference")
    public ResponseEntity<ApiResponse<Page<EventDto.Response>>> getYesterdayEvents(
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            // Get yesterday's date
            LocalDateTime yesterday = LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            
            Page<EventDto.Response> events = getEvents(yesterday, page, size);
            return ResponseEntity.ok(ApiResponse.success(events));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to fetch yesterday's events: " + e.getMessage()));
        }
    }

    @GetMapping("/tomorrow")
    @Operation(summary = "Get tomorrow's events", 
               description = "Get events for tomorrow based on user's language preference")
    public ResponseEntity<ApiResponse<Page<EventDto.Response>>> getTomorrowEvents(
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            // Get tomorrow's date
            LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            
            Page<EventDto.Response> events = getEvents(tomorrow, page, size);
            return ResponseEntity.ok(ApiResponse.success(events));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to fetch tomorrow's events: " + e.getMessage()));
        }
    }
}
