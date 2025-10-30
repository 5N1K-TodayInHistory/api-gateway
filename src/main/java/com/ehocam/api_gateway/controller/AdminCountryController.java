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
import com.ehocam.api_gateway.entity.Country;
import com.ehocam.api_gateway.repository.CountryRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/admin/countries")
@Tag(name = "Admin Countries", description = "Admin-only CRUD for countries")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCountryController {

    @Autowired
    private CountryRepository countryRepository;

    @GetMapping
    @Operation(summary = "List countries", description = "List all countries ordered by code")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List retrieved",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
    public ResponseEntity<ApiResponse<List<Country>>> list() {
        List<Country> countries = countryRepository.findAllByOrderByCodeAsc();
        return ResponseEntity.ok(ApiResponse.success(countries));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get country", description = "Get a country by ID")
    public ResponseEntity<ApiResponse<Country>> get(@PathVariable Long id) {
        return countryRepository.findById(id)
                .map(country -> ResponseEntity.ok(ApiResponse.success(country)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create country", description = "Create a new country")
    public ResponseEntity<ApiResponse<Country>> create(@Valid @RequestBody CreateOrUpdateCountry req) {
        Country c = new Country(req.code, req.name, req.flag);
        Country saved = countryRepository.save(c);
        return ResponseEntity.status(201).body(ApiResponse.success(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update country", description = "Update an existing country")
    public ResponseEntity<ApiResponse<Country>> update(@PathVariable Long id,
                                                       @Valid @RequestBody CreateOrUpdateCountry req) {
        return countryRepository.findById(id).map(existing -> {
            if (req.code != null) existing.setCode(req.code);
            if (req.name != null) existing.setName(req.name);
            if (req.flag != null) existing.setFlag(req.flag);
            Country saved = countryRepository.save(existing);
            return ResponseEntity.ok(ApiResponse.success(saved));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete country", description = "Delete a country by ID")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        if (!countryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        countryRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted"));
    }

    public static class CreateOrUpdateCountry {
        @NotBlank
        public String code;
        @NotNull
        public Map<String, String> name;
        @NotBlank
        public String flag;
    }
}


