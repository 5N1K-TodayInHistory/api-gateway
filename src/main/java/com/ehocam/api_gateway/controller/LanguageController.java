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

@RestController
@RequestMapping("/api/languages")
public class LanguageController {

    @Autowired
    private LanguageService languageService;

    /**
     * Get all supported languages with names in specified language
     * GET /api/languages?lang=tr
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<LanguageDto.Response>>> getAllLanguages(
            @RequestParam(value = "lang", defaultValue = "en") String language) {
        try {
            List<LanguageDto.Response> languages = languageService.getAllLanguages(language);
            return ResponseEntity.ok(ApiResponse.success(languages));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve languages: " + e.getMessage()));
        }
    }
}
