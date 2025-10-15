package com.ehocam.api_gateway.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ehocam.api_gateway.dto.EventDto;
import com.ehocam.api_gateway.entity.Event;
import com.ehocam.api_gateway.entity.User;
import com.ehocam.api_gateway.repository.EventRepository;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AuthService authService;

    /**
     * Get events based on user's language preference
     * @param username Username
     * @param page Page number
     * @param size Page size
     * @return Events filtered by user's language
     */
    public Page<EventDto.Response> getEventsForUser(String username, int page, int size) {
        // Get user and their language preference
        User user = authService.getUserByUsername(username);
        String userLanguage = getUserLanguage(user);
        
        // Get events sorted by date
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<Event> events = eventRepository.findAll(pageable);
        
        // Convert events to user's language
        return events.map(event -> convertToResponseForLanguage(event, userLanguage));
    }

    /**
     * Get events within a date range based on user's language preference
     * @param username Username
     * @param startDate Start date
     * @param endDate End date
     * @param page Page number
     * @param size Page size
     * @return Filtered events
     */
    public Page<EventDto.Response> getEventsForUserByDateRange(
            String username, 
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            int page, 
            int size) {
        
        User user = authService.getUserByUsername(username);
        String userLanguage = getUserLanguage(user);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<Event> events = eventRepository.findByDateBetween(startDate, endDate, pageable);
        
        return events.map(event -> convertToResponseForLanguage(event, userLanguage));
    }

    /**
     * Get events for a specific date (e.g., January 1st)
     * @param username Username
     * @param targetDate Target date
     * @param page Page number
     * @param size Page size
     * @return Events for the specific date
     */
    public Page<EventDto.Response> getEventsForUserByDate(
            String username, 
            LocalDateTime targetDate, 
            int page, 
            int size) {
        
        User user = authService.getUserByUsername(username);
        String userLanguage = getUserLanguage(user);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<Event> events = eventRepository.findByDate(targetDate, pageable);
        
        return events.map(event -> convertToResponseForLanguage(event, userLanguage));
    }

    /**
     * Convert event to Response DTO based on user's language
     * @param event Event entity
     * @param userLanguage User's language preference
     * @return Converted EventDto.Response
     */
    private EventDto.Response convertToResponseForLanguage(Event event, String userLanguage) {
        EventDto.Response response = new EventDto.Response();
        response.setId(event.getId());
        response.setDate(event.getDate());
        response.setCategory(event.getCategory());
        response.setCountry(event.getCountry());
        response.setRatio(event.getRatio());
        response.setCreatedAt(event.getCreatedAt());
        response.setUpdatedAt(event.getUpdatedAt());

        // Set content based on user's language
        setLocalizedContent(event, response, userLanguage);
        
        // Set media based on user's language
        setLocalizedMedia(event, response, userLanguage);
        
        // Set engagement information
        setEngagement(event, response);

        return response;
    }

    /**
     * Set event content based on user's language
     * @param event Event entity
     * @param response Response DTO
     * @param userLanguage User's language preference
     */
    private void setLocalizedContent(Event event, EventDto.Response response, String userLanguage) {
        // First check i18n content
        if (event.getI18n() != null && event.getI18n().containsKey(userLanguage)) {
            Event.I18nContent i18nContent = event.getI18n().get(userLanguage);
            response.setTitle(i18nContent.getTitle());
            response.setSummary(i18nContent.getSummary());
            response.setContent(i18nContent.getContent());
        } else {
            // Fallback: Default language (English) or original content
            String fallbackLanguage = "en";
            if (event.getI18n() != null && event.getI18n().containsKey(fallbackLanguage)) {
                Event.I18nContent fallbackContent = event.getI18n().get(fallbackLanguage);
                response.setTitle(fallbackContent.getTitle());
                response.setSummary(fallbackContent.getSummary());
                response.setContent(fallbackContent.getContent());
            } else {
                // Final fallback: Original content
                response.setTitle(event.getTitle());
                response.setSummary(event.getSummary());
                response.setContent(event.getContent());
            }
        }
    }

    /**
     * Set media based on user's language
     * @param event Event entity
     * @param response Response DTO
     * @param userLanguage User's language preference
     */
    private void setLocalizedMedia(Event event, EventDto.Response response, String userLanguage) {
        if (event.getMedia() != null) {
            EventDto.MediaDto mediaDto = new EventDto.MediaDto();
            mediaDto.setThumbnailUrl(event.getMedia().getThumbnailUrl());
            mediaDto.setBannerUrl(event.getMedia().getBannerUrl());
            mediaDto.setYoutubeId(event.getMedia().getYoutubeId());
            
            // Set audio file based on language
            if (event.getMedia().getI18n() != null && 
                event.getMedia().getI18n().containsKey(userLanguage)) {
                Event.MediaI18n mediaI18n = event.getMedia().getI18n().get(userLanguage);
                mediaDto.setAudioUrl(mediaI18n.getAudioUrl());
            } else {
                // Fallback: Default audio file
                String fallbackLanguage = "en";
                if (event.getMedia().getI18n() != null && 
                    event.getMedia().getI18n().containsKey(fallbackLanguage)) {
                    Event.MediaI18n fallbackMedia = event.getMedia().getI18n().get(fallbackLanguage);
                    mediaDto.setAudioUrl(fallbackMedia.getAudioUrl());
                } else {
                    // Final fallback: Original audio file
                    mediaDto.setAudioUrl(event.getMedia().getAudioUrl());
                }
            }
            
            response.setMedia(mediaDto);
        }
    }

    /**
     * Set engagement information
     * @param event Event entity
     * @param response Response DTO
     */
    private void setEngagement(Event event, EventDto.Response response) {
        if (event.getEngagement() != null) {
            EventDto.EngagementDto engagementDto = new EventDto.EngagementDto();
            engagementDto.setLikes(event.getEngagement().getLikes());
            engagementDto.setComments(event.getEngagement().getComments());
            engagementDto.setShares(event.getEngagement().getShares());
            response.setEngagement(engagementDto);
        }
    }

    /**
     * Get user's language preference
     * @param user User
     * @return Language code (e.g., "tr", "en")
     */
    private String getUserLanguage(User user) {
        if (user.getPreferences() != null && 
            user.getPreferences().getLanguage() != null && 
            !user.getPreferences().getLanguage().isEmpty()) {
            return user.getPreferences().getLanguage();
        }
        return "en"; // Default language
    }
}
