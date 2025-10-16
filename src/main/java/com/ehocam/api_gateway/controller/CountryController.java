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
import com.ehocam.api_gateway.dto.CountryDto;
import com.ehocam.api_gateway.service.CountryService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/countries")
@Tag(name = "Countries", description = "Country management endpoints with multilingual support")
public class CountryController {

    @Autowired
    private CountryService countryService;

    /**
     * Get all countries with names in specified language
     * GET /api/countries?lang=tr
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CountryDto.Response>>> getAllCountries(
            @RequestParam(value = "lang", defaultValue = "en") String language) {
        try {
            List<CountryDto.Response> countries = countryService.getAllCountries(language);
            return ResponseEntity.ok(ApiResponse.success(countries));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve countries: " + e.getMessage()));
        }
    }
}