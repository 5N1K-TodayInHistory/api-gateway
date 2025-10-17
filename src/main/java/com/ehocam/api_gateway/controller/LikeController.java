package com.ehocam.api_gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
@Tag(name = "Event Likes", description = "Event like/unlike management endpoints")
public class LikeController {

    @Autowired
    private EventService eventService;

    /**
     * Like an event
     */
    @PostMapping("/{eventId}/like")
    @Operation(summary = "Like an event", description = "Add a like to an event")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully liked event",
                       content = @Content(mediaType = "application/json", 
                                        schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<ApiResponse<EventDto.LikeResponse>> likeEvent(
            @Parameter(description = "Event ID to like") @PathVariable Long eventId) {
        
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated to like events");
        }

        EventDto.LikeResponse response = eventService.likeEvent(eventId, userId);
        if (!response.getSuccess()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to like event");
        }

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Unlike an event
     */
    @DeleteMapping("/{eventId}/like")
    @Operation(summary = "Unlike an event", description = "Remove a like from an event")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully unliked event",
                       content = @Content(mediaType = "application/json", 
                                        schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<ApiResponse<EventDto.LikeResponse>> unlikeEvent(
            @Parameter(description = "Event ID to unlike") @PathVariable Long eventId) {
        
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated to unlike events");
        }

        EventDto.LikeResponse response = eventService.unlikeEvent(eventId, userId);
        if (!response.getSuccess()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to unlike event");
        }

        return ResponseEntity.ok(ApiResponse.success(response));
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
