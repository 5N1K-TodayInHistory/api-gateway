package com.ehocam.api_gateway.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ehocam.api_gateway.dto.ApiResponse;
import com.ehocam.api_gateway.entity.EventType;
import com.ehocam.api_gateway.repository.EventTypeRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/admin/event-types")
@Tag(name = "Admin Event Types", description = "Admin-only CRUD for event types")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminEventTypeController {

    @Autowired
    private EventTypeRepository eventTypeRepository;

    @GetMapping
    @Operation(summary = "List event types", description = "List all event types ordered by code")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List retrieved",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
    public ResponseEntity<ApiResponse<List<EventType>>> list() {
        List<EventType> items = eventTypeRepository.findAllByOrderByCodeAsc();
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event type", description = "Get an event type by ID")
    public ResponseEntity<ApiResponse<EventType>> get(@PathVariable Long id) {
        return eventTypeRepository.findById(id)
                .map(item -> ResponseEntity.ok(ApiResponse.success(item)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create event type", description = "Create a new event type")
    public ResponseEntity<ApiResponse<EventType>> create(@Valid @RequestBody CreateOrUpdateEventType req) {
        EventType e = new EventType(req.code, req.name);
        EventType saved = eventTypeRepository.save(e);
        return ResponseEntity.status(201).body(ApiResponse.success(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update event type", description = "Update an existing event type")
    public ResponseEntity<ApiResponse<EventType>> update(@PathVariable Long id,
                                                         @Valid @RequestBody CreateOrUpdateEventType req) {
        return eventTypeRepository.findById(id).map(existing -> {
            if (req.code != null) existing.setCode(req.code);
            if (req.name != null) existing.setName(req.name);
            EventType saved = eventTypeRepository.save(existing);
            return ResponseEntity.ok(ApiResponse.success(saved));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete event type", description = "Delete an event type by ID")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        if (!eventTypeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        eventTypeRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted"));
    }

    public static class CreateOrUpdateEventType {
        @NotBlank
        public String code;
        @NotNull
        public Map<String, String> name;
    }
}


