package com.ehocam.api_gateway.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ehocam.api_gateway.dto.ApiResponse;
import com.ehocam.api_gateway.dto.EventTypeDto;
import com.ehocam.api_gateway.service.EventTypeService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/event-types")
@Tag(name = "Event Types", description = "Event type management endpoints with multilingual support")
public class EventTypeController {

    @Autowired
    private EventTypeService eventTypeService;

    /**
     * Get all supported event types with names in specified language
     * GET /api/event-types?lang=tr
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventTypeDto.Response>>> getAllEventTypes(
            @RequestParam(value = "lang", defaultValue = "en") String language) {
        try {
            List<EventTypeDto.Response> eventTypes = eventTypeService.getAllEventTypes(language);
            return ResponseEntity.ok(ApiResponse.success(eventTypes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve event types: " + e.getMessage()));
        }
    }
}
