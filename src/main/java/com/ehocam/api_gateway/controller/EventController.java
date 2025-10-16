package com.ehocam.api_gateway.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ehocam.api_gateway.dto.ApiResponse;
import com.ehocam.api_gateway.dto.EventDto;
import com.ehocam.api_gateway.service.EventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Event management endpoints with multilingual support")
public class EventController {

    @Autowired
    private EventService eventService;

    /**
     * Get today's events with pagination and filters
     */
    @GetMapping("/today")
    @Operation(summary = "Get today's events", description = "Retrieve today's events with pagination and filters")
    public ResponseEntity<ApiResponse<Page<EventDto.Response>>> getTodaysEvents(
            @Parameter(description = "Language code for multilingual content") @RequestParam(value = "lang", defaultValue = "en") String language,
            @Parameter(description = "Event type filter") @RequestParam(value = "type", required = false) String type,
            @Parameter(description = "Country filter") @RequestParam(value = "country", required = false) String country,
            @Parameter(description = "Page number (0-based)") @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(value = "size", defaultValue = "20") int size,
            @Parameter(description = "Sort order") @RequestParam(value = "sort", defaultValue = "DATE_DESC") String sort) {
        
        Long userId = getCurrentUserId();
        Page<EventDto.Response> events = eventService.getTodaysEvents(language, type, country, page, size, sort, userId);
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    /**
     * Get events by specific date
     */
    @GetMapping("/date/{date}")
    @Operation(summary = "Get events by date", description = "Retrieve events for a specific date")
    public ResponseEntity<ApiResponse<Page<EventDto.Response>>> getEventsByDate(
            @Parameter(description = "Date in YYYY-MM-DD format") @PathVariable String date,
            @Parameter(description = "Language code for multilingual content") @RequestParam(value = "lang", defaultValue = "en") String language,
            @Parameter(description = "Event type filter") @RequestParam(value = "type", required = false) String type,
            @Parameter(description = "Country filter") @RequestParam(value = "country", required = false) String country,
            @Parameter(description = "Page number (0-based)") @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(value = "size", defaultValue = "20") int size,
            @Parameter(description = "Sort order") @RequestParam(value = "sort", defaultValue = "DATE_DESC") String sort) {
        
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date + "T00:00:00");
            Long userId = getCurrentUserId();
            Page<EventDto.Response> events = eventService.getEventsByDate(dateTime, language, type, country, page, size, sort, userId);
            return ResponseEntity.ok(ApiResponse.success(events));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format. Use YYYY-MM-DD");
        }
    }

    /**
     * Get events by date range
     */
    @GetMapping("/range")
    @Operation(summary = "Get events by date range", description = "Retrieve events within a date range")
    public ResponseEntity<ApiResponse<Page<EventDto.Response>>> getEventsByDateRange(
            @Parameter(description = "Start date in YYYY-MM-DD format") @RequestParam String startDate,
            @Parameter(description = "End date in YYYY-MM-DD format") @RequestParam String endDate,
            @Parameter(description = "Language code for multilingual content") @RequestParam(value = "lang", defaultValue = "en") String language,
            @Parameter(description = "Event type filter") @RequestParam(value = "type", required = false) String type,
            @Parameter(description = "Country filter") @RequestParam(value = "country", required = false) String country,
            @Parameter(description = "Page number (0-based)") @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(value = "size", defaultValue = "20") int size,
            @Parameter(description = "Sort order") @RequestParam(value = "sort", defaultValue = "DATE_DESC") String sort) {
        
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime endDateTime = LocalDateTime.parse(endDate + "T23:59:59");
            Long userId = getCurrentUserId();
            Page<EventDto.Response> events = eventService.getEventsByDateRange(startDateTime, endDateTime, language, type, country, page, size, sort, userId);
            return ResponseEntity.ok(ApiResponse.success(events));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format. Use YYYY-MM-DD");
        }
    }

    /**
     * Get a single event by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID", description = "Retrieve a specific event by its ID")
    public ResponseEntity<ApiResponse<EventDto.Response>> getEventById(
            @Parameter(description = "Event ID") @PathVariable Long id,
            @Parameter(description = "Language code for multilingual content") @RequestParam(value = "lang", defaultValue = "en") String language) {
        
        Long userId = getCurrentUserId();
        Optional<EventDto.Response> event = eventService.getEventById(id, language, userId);
        return event.map(e -> ResponseEntity.ok(ApiResponse.success(e)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    /**
     * Get current user ID from authentication context
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            try {
                return Long.parseLong(authentication.getName());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}