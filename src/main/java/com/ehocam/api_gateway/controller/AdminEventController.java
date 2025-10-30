package com.ehocam.api_gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ehocam.api_gateway.dto.AdminEventDto;
import com.ehocam.api_gateway.entity.Event;
import com.ehocam.api_gateway.repository.EventRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/events")
@Tag(name = "Admin Events", description = "Admin-only event management endpoints")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminEventController {

    @Autowired
    private EventRepository eventRepository;

    @PostMapping
    @Operation(summary = "Create new event", description = "Create a new event (Admin only)")
    @ApiResponse(responseCode = "201", description = "Event created successfully",
                       content = @Content(mediaType = "application/json", 
                                        schema = @Schema(implementation = AdminEventDto.Response.class)))
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    public ResponseEntity<com.ehocam.api_gateway.dto.ApiResponse<AdminEventDto.Response>> createEvent(
            @Valid @RequestBody AdminEventDto.Create request) {
        try {
            Event event = new Event();
            event.setTitle(request.getTitle());
            event.setDescription(request.getDescription());
            event.setContent(request.getContent());
            event.setDate(request.getDate());
            event.setType(request.getType());
            event.setCountry(request.getCountry());
            event.setImageUrl(request.getImageUrl());
            event.setVideoUrls(request.getVideoUrls());
            event.setAudioUrls(request.getAudioUrls());
            event.setScore(request.getScore());
            event.setImportanceReason(request.getImportanceReason());
            event.setLikesCount(0);
            event.setCommentsCount(0);

            Event savedEvent = eventRepository.save(event);
            AdminEventDto.Response response = new AdminEventDto.Response(savedEvent);
            
            return ResponseEntity.status(201)
                    .body(com.ehocam.api_gateway.dto.ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(com.ehocam.api_gateway.dto.ApiResponse.error("Failed to create event: " + e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get all events", description = "Get paginated list of all events (Admin only)")
    @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    public ResponseEntity<com.ehocam.api_gateway.dto.ApiResponse<Page<AdminEventDto.Response>>> getAllEvents(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Event> events = eventRepository.findAll(pageable);
            Page<AdminEventDto.Response> response = events.map(AdminEventDto.Response::new);
            
            return ResponseEntity.ok(com.ehocam.api_gateway.dto.ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(com.ehocam.api_gateway.dto.ApiResponse.error("Failed to retrieve events: " + e.getMessage()));
        }
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Get event by ID", description = "Get specific event by ID (Admin only)")
    @ApiResponse(responseCode = "200", description = "Event retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Event not found")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    public ResponseEntity<com.ehocam.api_gateway.dto.ApiResponse<AdminEventDto.Response>> getEventById(
            @Parameter(description = "Event ID") @PathVariable Long id) {
        try {
            Event event = eventRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
            
            AdminEventDto.Response response = new AdminEventDto.Response(event);
            return ResponseEntity.ok(com.ehocam.api_gateway.dto.ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(com.ehocam.api_gateway.dto.ApiResponse.error("Failed to retrieve event: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update event", description = "Update existing event (Admin only)")
    @ApiResponse(responseCode = "200", description = "Event updated successfully")
    @ApiResponse(responseCode = "404", description = "Event not found")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    public ResponseEntity<com.ehocam.api_gateway.dto.ApiResponse<AdminEventDto.Response>> updateEvent(
            @Parameter(description = "Event ID") @PathVariable Long id,
            @Valid @RequestBody AdminEventDto.Update request) {
        try {
            Event event = eventRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

            // Update fields only if provided
            if (request.getTitle() != null) event.setTitle(request.getTitle());
            if (request.getDescription() != null) event.setDescription(request.getDescription());
            if (request.getContent() != null) event.setContent(request.getContent());
            if (request.getDate() != null) event.setDate(request.getDate());
            if (request.getType() != null) event.setType(request.getType());
            if (request.getCountry() != null) event.setCountry(request.getCountry());
            if (request.getImageUrl() != null) event.setImageUrl(request.getImageUrl());
            if (request.getVideoUrls() != null) event.setVideoUrls(request.getVideoUrls());
            if (request.getAudioUrls() != null) event.setAudioUrls(request.getAudioUrls());
            if (request.getScore() != null) event.setScore(request.getScore());
            if (request.getImportanceReason() != null) event.setImportanceReason(request.getImportanceReason());

            Event savedEvent = eventRepository.save(event);
            AdminEventDto.Response response = new AdminEventDto.Response(savedEvent);
            
            return ResponseEntity.ok(com.ehocam.api_gateway.dto.ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(com.ehocam.api_gateway.dto.ApiResponse.error("Failed to update event: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete event", description = "Delete event by ID (Admin only)")
    @ApiResponse(responseCode = "200", description = "Event deleted successfully")
    @ApiResponse(responseCode = "404", description = "Event not found")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    public ResponseEntity<com.ehocam.api_gateway.dto.ApiResponse<String>> deleteEvent(
            @Parameter(description = "Event ID") @PathVariable Long id) {
        try {
            if (!eventRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            eventRepository.deleteById(id);
            return ResponseEntity.ok(com.ehocam.api_gateway.dto.ApiResponse.success("Event deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(com.ehocam.api_gateway.dto.ApiResponse.error("Failed to delete event: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search events", description = "Search events with filters (Admin only)")
    @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    public ResponseEntity<com.ehocam.api_gateway.dto.ApiResponse<Page<AdminEventDto.Response>>> searchEvents(
            @Parameter(description = "Event type") @RequestParam(required = false) String type,
            @Parameter(description = "Country code") @RequestParam(required = false) String country,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Event> events;
            if (type != null && country != null) {
                events = eventRepository.findByTypeAndCountryOrderByDateDesc(type, country, pageable);
            } else if (type != null) {
                events = eventRepository.findByTypeOrderByDateDesc(type, pageable);
            } else if (country != null) {
                events = eventRepository.findByCountryOrderByDateDesc(country, pageable);
            } else {
                events = eventRepository.findAll(pageable);
            }
            
            Page<AdminEventDto.Response> response = events.map(AdminEventDto.Response::new);
            return ResponseEntity.ok(com.ehocam.api_gateway.dto.ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(com.ehocam.api_gateway.dto.ApiResponse.error("Failed to search events: " + e.getMessage()));
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Get event statistics", description = "Get event statistics (Admin only)")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    public ResponseEntity<com.ehocam.api_gateway.dto.ApiResponse<EventStats>> getEventStats() {
        try {
            long totalEvents = eventRepository.count();
            long totalLikes = eventRepository.findAll().stream()
                    .mapToLong(event -> event.getLikesCount() != null ? event.getLikesCount().longValue() : 0L)
                    .sum();
            long totalComments = eventRepository.findAll().stream()
                    .mapToLong(event -> event.getCommentsCount() != null ? event.getCommentsCount().longValue() : 0L)
                    .sum();
            
            EventStats stats = new EventStats(totalEvents, totalLikes, totalComments);
            return ResponseEntity.ok(com.ehocam.api_gateway.dto.ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(com.ehocam.api_gateway.dto.ApiResponse.error("Failed to retrieve statistics: " + e.getMessage()));
        }
    }

    // Inner class for statistics
    public static class EventStats {
        private final long totalEvents;
        private final long totalLikes;
        private final long totalComments;

        public EventStats(long totalEvents, long totalLikes, long totalComments) {
            this.totalEvents = totalEvents;
            this.totalLikes = totalLikes;
            this.totalComments = totalComments;
        }

        // Getters
        public long getTotalEvents() { return totalEvents; }
        public long getTotalLikes() { return totalLikes; }
        public long getTotalComments() { return totalComments; }
    }
}