package com.ehocam.api_gateway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
     * Get all countries with names in specified language
     */
    @Transactional(readOnly = true)
    public List<CountryDto.Response> getAllCountries(String language) {
        List<Country> countries = countryRepository.findAllByOrderByCodeAsc();
        return countries.stream()
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