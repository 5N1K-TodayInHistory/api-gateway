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
import com.ehocam.api_gateway.entity.Language;
import com.ehocam.api_gateway.repository.LanguageRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/admin/languages")
@Tag(name = "Admin Languages", description = "Admin-only CRUD for languages")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminLanguageController {

    @Autowired
    private LanguageRepository languageRepository;

    @GetMapping
    @Operation(summary = "List languages", description = "List all languages ordered by code")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List retrieved",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
    public ResponseEntity<ApiResponse<List<Language>>> list() {
        List<Language> languages = languageRepository.findAllByOrderByCodeAsc();
        return ResponseEntity.ok(ApiResponse.success(languages));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get language", description = "Get a language by ID")
    public ResponseEntity<ApiResponse<Language>> get(@PathVariable Long id) {
        return languageRepository.findById(id)
                .map(lang -> ResponseEntity.ok(ApiResponse.success(lang)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create language", description = "Create a new language")
    public ResponseEntity<ApiResponse<Language>> create(@Valid @RequestBody CreateOrUpdateLanguage req) {
        Language l = new Language(req.code, req.name);
        Language saved = languageRepository.save(l);
        return ResponseEntity.status(201).body(ApiResponse.success(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update language", description = "Update an existing language")
    public ResponseEntity<ApiResponse<Language>> update(@PathVariable Long id,
                                                        @Valid @RequestBody CreateOrUpdateLanguage req) {
        return languageRepository.findById(id).map(existing -> {
            if (req.code != null) existing.setCode(req.code);
            if (req.name != null) existing.setName(req.name);
            Language saved = languageRepository.save(existing);
            return ResponseEntity.ok(ApiResponse.success(saved));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete language", description = "Delete a language by ID")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        if (!languageRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        languageRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted"));
    }

    public static class CreateOrUpdateLanguage {
        @NotBlank
        public String code;
        @NotNull
        public Map<String, String> name;
    }
}


