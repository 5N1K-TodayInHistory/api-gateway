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
import com.ehocam.api_gateway.dto.LanguageDto;
import com.ehocam.api_gateway.service.LanguageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/languages")
@Tag(name = "Languages", description = "Language management endpoints with multilingual support")
public class LanguageController {

    @Autowired
    private LanguageService languageService;

    /**
     * Get all supported languages with names in specified language
     * GET /api/languages?lang=tr
     */
    @GetMapping
    @Operation(summary = "Get all languages", description = "Retrieve all supported languages with multilingual names")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved languages",
                       content = @Content(mediaType = "application/json", 
                                        schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<ApiResponse<List<LanguageDto.Response>>> getAllLanguages(
            @Parameter(description = "Language code for multilingual content", example = "en") @RequestParam(value = "lang", defaultValue = "en") String language) {
        try {
            List<LanguageDto.Response> languages = languageService.getAllLanguages(language);
            return ResponseEntity.ok(ApiResponse.success(languages));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve languages: " + e.getMessage()));
        }
    }
}
