package com.ehocam.api_gateway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ehocam.api_gateway.dto.LanguageDto;
import com.ehocam.api_gateway.entity.Language;
import com.ehocam.api_gateway.repository.LanguageRepository;

@Service
@Transactional
public class LanguageService {

    @Autowired
    private LanguageRepository languageRepository;

    /**
     * Get all languages with names in specified language
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "languages", key = "#language")
    public List<LanguageDto.Response> getAllLanguages(String language) {
        List<Language> languages = languageRepository.findAllByOrderByCodeAsc();
        return languages.stream()
                .map(lang -> convertToResponse(lang, language))
                .collect(Collectors.toList());
    }

    /**
     * Get supported locales (language codes) for the application
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "supportedLocales", key = "#language")
    public List<String> getSupportedLocales(String language) {
        List<Language> languages = languageRepository.findAllByOrderByCodeAsc();
        return languages.stream()
                .map(Language::getCode)
                .collect(Collectors.toList());
    }

    /**
     * Convert Language entity to Response DTO with specific language
     */
    private LanguageDto.Response convertToResponse(Language language, String languageCode) {
        String name = language.getNameForLanguage(languageCode);
        if (name == null) {
            // Fallback to default name if requested language not available
            name = language.getDefaultName();
        }
        
        return new LanguageDto.Response(
                language.getCode(),
                name
        );
    }
}
