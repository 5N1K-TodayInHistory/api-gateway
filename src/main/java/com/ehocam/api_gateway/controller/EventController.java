package com.ehocam.api_gateway.controller;

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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Event management endpoints with multilingual support")
public class EventController {

    @Autowired
    private EventService eventService;

    /**
     * Get today's events with pagination and filters (optimized version)
     */
    @GetMapping("/today")
    @Operation(summary = "Get today's events", description = "Retrieve today's events with pagination and filters. Uses optimized month_day queries for better performance.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved events",
                       content = @Content(mediaType = "application/json", 
                                        schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<ApiResponse<Page<EventDto.Response>>> getTodaysEvents(
            @Parameter(description = "Language code for multilingual content") @RequestParam(value = "lang", defaultValue = "en") String language,
            @Parameter(description = "Event type filter") @RequestParam(value = "type", required = false) String type,
            @Parameter(description = "Country filter") @RequestParam(value = "country", required = false) String country,
            @Parameter(description = "Page number (0-based)") @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(value = "size", defaultValue = "20") int size,
            @Parameter(description = "Sort order") @RequestParam(value = "sort", defaultValue = "DATE_DESC") String sort) {
        
        // Use optimized method if country is specified and type is not specified
        // Otherwise use standard method which supports both type and country filters
        if (country != null && !country.isEmpty() && (type == null || type.isEmpty())) {
            Long userId = getCurrentUserId();
            Page<EventDto.Response> events = eventService.findTodayByCountry(country, language, page, size, userId);
            return ResponseEntity.ok(ApiResponse.success(events));
        }
        
        return getEventsForDay(0, type, country, page, size, sort, language);
    }

    /**
     * Get tomorrow's events with pagination and filters (optimized version)
     */
    @GetMapping("/tomorrow")
    @Operation(summary = "Get tomorrow's events", description = "Retrieve tomorrow's events with pagination and filters. Uses optimized month_day queries for better performance.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved events",
                       content = @Content(mediaType = "application/json", 
                                        schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<ApiResponse<Page<EventDto.Response>>> getTomorrowsEvents(
            @Parameter(description = "Language code for multilingual content") @RequestParam(value = "lang", defaultValue = "en") String language,
            @Parameter(description = "Event type filter") @RequestParam(value = "type", required = false) String type,
            @Parameter(description = "Country filter") @RequestParam(value = "country", required = false) String country,
            @Parameter(description = "Page number (0-based)") @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(value = "size", defaultValue = "20") int size,
            @Parameter(description = "Sort order") @RequestParam(value = "sort", defaultValue = "DATE_DESC") String sort) {
        
        // Use optimized method if country is specified and type is not specified
        // Otherwise use standard method which supports both type and country filters
        if (country != null && !country.isEmpty() && (type == null || type.isEmpty())) {
            Long userId = getCurrentUserId();
            Page<EventDto.Response> events = eventService.findTomorrowByCountry(country, language, page, size, userId);
            return ResponseEntity.ok(ApiResponse.success(events));
        }
        
        return getEventsForDay(1, type, country, page, size, sort, language);
    }

    /**
     * Get yesterday's events with pagination and filters (optimized version)
     */
    @GetMapping("/yesterday")
    @Operation(summary = "Get yesterday's events", description = "Retrieve yesterday's events with pagination and filters. Uses optimized month_day queries for better performance.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved events",
                       content = @Content(mediaType = "application/json", 
                                        schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<ApiResponse<Page<EventDto.Response>>> getYesterdaysEvents(
            @Parameter(description = "Language code for multilingual content") @RequestParam(value = "lang", defaultValue = "en") String language,
            @Parameter(description = "Event type filter") @RequestParam(value = "type", required = false) String type,
            @Parameter(description = "Country filter") @RequestParam(value = "country", required = false) String country,
            @Parameter(description = "Page number (0-based)") @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(value = "size", defaultValue = "20") int size,
            @Parameter(description = "Sort order") @RequestParam(value = "sort", defaultValue = "DATE_DESC") String sort) {
        
        // Use optimized method if country is specified and type is not specified
        // Otherwise use standard method which supports both type and country filters
        if (country != null && !country.isEmpty() && (type == null || type.isEmpty())) {
            Long userId = getCurrentUserId();
            Page<EventDto.Response> events = eventService.findYesterdayByCountry(country, language, page, size, userId);
            return ResponseEntity.ok(ApiResponse.success(events));
        }
        
        return getEventsForDay(-1, type, country, page, size, sort, language);
    }

    /**
     * Get a single event by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID", description = "Retrieve a specific event by its ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved event",
                       content = @Content(mediaType = "application/json", 
                                        schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Event not found")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<ApiResponse<EventDto.Response>> getEventById(
            @Parameter(description = "Event ID") @PathVariable Long id,
            @Parameter(description = "Language code for multilingual content") @RequestParam(value = "lang", defaultValue = "en") String language) {
        
        Long userId = getCurrentUserId();
        Optional<EventDto.Response> event = eventService.getEventById(id, language, userId);
        return event.map(e -> ResponseEntity.ok(ApiResponse.success(e)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    /**
     * Common method to get events for a specific day
     */
    private ResponseEntity<ApiResponse<Page<EventDto.Response>>> getEventsForDay(int dayOffset, String type, String country, int page, int size, String sort, String language) {
        Long userId = getCurrentUserId();
        Page<EventDto.Response> events = eventService.getEventsForDay(dayOffset, type, country, page, size, sort, userId, language);
        return ResponseEntity.ok(ApiResponse.success(events));
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