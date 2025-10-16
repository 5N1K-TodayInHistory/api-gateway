package com.ehocam.api_gateway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ehocam.api_gateway.dto.EventTypeDto;
import com.ehocam.api_gateway.entity.EventType;
import com.ehocam.api_gateway.repository.EventTypeRepository;

@Service
@Transactional
public class EventTypeService {

    @Autowired
    private EventTypeRepository eventTypeRepository;

    /**
     * Get all event types with names in specified language
     */
    @Transactional(readOnly = true)
    public List<EventTypeDto.Response> getAllEventTypes(String language) {
        List<EventType> eventTypes = eventTypeRepository.findAllByOrderByCodeAsc();
        return eventTypes.stream()
                .map(eventType -> convertToResponse(eventType, language))
                .collect(Collectors.toList());
    }

    /**
     * Convert EventType entity to Response DTO with specific language
     */
    private EventTypeDto.Response convertToResponse(EventType eventType, String languageCode) {
        String name = eventType.getNameForLanguage(languageCode);
        if (name == null) {
            // Fallback to default name if requested language not available
            name = eventType.getDefaultName();
        }
        
        return new EventTypeDto.Response(
                eventType.getCode(),
                name
        );
    }
}
