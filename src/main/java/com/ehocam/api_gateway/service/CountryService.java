package com.ehocam.api_gateway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ehocam.api_gateway.dto.CountryDto;
import com.ehocam.api_gateway.entity.Country;
import com.ehocam.api_gateway.repository.CountryRepository;

@Service
@Transactional
public class CountryService {

    @Autowired
    private CountryRepository countryRepository;

    /**
     * Get all active countries with names in specified language
     * Returns Global first, then other countries alphabetically
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "countries", key = "#language")
    public List<CountryDto.Response> getAllCountries(String language) {
        List<Country> countries = countryRepository.findAllByIsActiveTrueOrderByCodeAsc();
        
        // Sort countries: Global first, then alphabetically by name
        List<Country> sortedCountries = countries.stream()
                .sorted((c1, c2) -> {
                    // Global (ALL) should come first
                    if ("ALL".equals(c1.getCode())) return -1;
                    if ("ALL".equals(c2.getCode())) return 1;
                    // Other countries sorted alphabetically by name
                    String name1 = c1.getNameForLanguage(language);
                    if (name1 == null) name1 = c1.getDefaultName();
                    String name2 = c2.getNameForLanguage(language);
                    if (name2 == null) name2 = c2.getDefaultName();
                    return name1.compareToIgnoreCase(name2);
                })
                .collect(Collectors.toList());
        
        
        return sortedCountries.stream()
                .map(country -> convertToResponse(country, language))
                .collect(Collectors.toList());
    }

    /**
     * Convert Country entity to Response DTO with specific language
     */
    private CountryDto.Response convertToResponse(Country country, String language) {
        String name = country.getNameForLanguage(language);
        if (name == null) {
            // Fallback to default name if requested language not available
            name = country.getDefaultName();
        }
        
        return new CountryDto.Response(
                country.getCode(),
                name,
                country.getFlag()
        );
    }
}