package com.ehocam.api_gateway.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ehocam.api_gateway.cache.CacheWrapper;

import com.ehocam.api_gateway.dto.EventDto;
import com.ehocam.api_gateway.entity.Event;
import com.ehocam.api_gateway.entity.EventLike;
import com.ehocam.api_gateway.entity.EventReference;
import com.ehocam.api_gateway.repository.EventLikeRepository;
import com.ehocam.api_gateway.repository.EventReferenceRepository;
import com.ehocam.api_gateway.repository.EventRepository;
import com.ehocam.api_gateway.repository.UserRepository;

@Service
@Transactional
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private EventLikeRepository eventLikeRepository;
    
    @Autowired
    private EventReferenceRepository eventReferenceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CacheManager cacheManager;
    
    @Autowired
    private CacheWrapper cacheWrapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    /**
     * Get user's preferred language or fallback to default
     */
    private String getUserLanguage(Long userId, String fallbackLanguage) {
        if (userId == null) {
            return fallbackLanguage != null ? fallbackLanguage : "en";
        }
        
        return userRepository.findById(userId)
                .map(user -> user.getPreferences().getLanguage())
                .orElse(fallbackLanguage != null ? fallbackLanguage : "en");
    }

    /**
     * Get events for a specific day (today, yesterday, tomorrow) with pagination and filters
     */
    @Transactional(readOnly = true)
    public Page<EventDto.Response> getEventsForDay(int dayOffset, String type, String country, 
                                                   int page, int size, String sort, Long userId, String language) {
        Pageable pageable = PageRequest.of(page, size);
        
        // Use provided language parameter, fallback to user's preferred language
        String userLanguage = language != null ? language : getUserLanguage(userId, "en");
        
        // Get date range for the specified day
        LocalDateTime startOfDay = LocalDateTime.now()
                .plusDays(dayOffset)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        Page<Event> events;

        if (type != null && country != null) {
            events = eventRepository.findTodaysEventsByTypeAndCountry(startOfDay, endOfDay, type, country, pageable);
        } else if (type != null) {
            events = eventRepository.findTodaysEventsByType(startOfDay, endOfDay, type, pageable);
        } else if (country != null) {
            events = eventRepository.findTodaysEventsByCountry(startOfDay, endOfDay, country, pageable);
        } else {
            events = eventRepository.findTodaysEvents(startOfDay, endOfDay, pageable);
        }

        return events.map(event -> convertToResponse(event, userLanguage));
    }

    /**
     * Get today's events with pagination and filters
     * Using CacheWrapper for clean and type-safe cache operations
     */
    @Transactional(readOnly = true)
    public Page<EventDto.Response> getTodaysEvents(String language, String type, String country, 
                                                    int page, int size, String sort, Long userId) {
        // Generate cache key
        String cacheKey = generateTodayCacheKey(country, type, page, size, language);
        
        // Use CacheWrapper for clean cache operations
        return cacheWrapper.getOrCompute(
            "todayByCountry", 
            cacheKey, 
            Page.class,
            () -> getEventsForDay(0, type, country, page, size, sort, userId, language)
        );
    }
    
    /**
     * Generate cache key for today's events
     */
    private String generateTodayCacheKey(String country, String type, int page, int size, String language) {
        LocalDate today = LocalDate.now();
        return String.format("%s:%s:%d-%d:%s:%d:%d:%s", 
            country, type, today.getMonthValue(), today.getDayOfMonth(), 
            page, size, language);
    }
    
    /**
     * Example of using addOrUpdate - CacheManager.michaco.net style
     * Update event view count with cache-aware operations
     */
    public void incrementEventViewCount(Long eventId) {
        String cacheKey = "event:views:" + eventId;
        
        // AddOrUpdate pattern - if key exists, increment, otherwise set to 1
        cacheWrapper.addOrUpdate(
            "eventStats", 
            cacheKey, 
            1, // Default value if key doesn't exist
            currentCount -> currentCount + 1 // Update function if key exists
        );
    }
    
    /**
     * Example of using getOrCompute for expensive operations
     * Get trending events with cache-aware computation
     */
    public List<EventDto.Response> getTrendingEventsCached(String language, int limit, Long userId) {
        String cacheKey = "trending:" + language + ":" + limit;
        
        return cacheWrapper.getOrCompute(
            "trendingEvents",
            cacheKey,
            List.class,
            () -> {
                // Expensive operation - only runs on cache miss
                return getTrendingEvents(language, 0, limit, userId).getContent();
            }
        );
    }

    /**
     * Get events by date with pagination and filters
     */
    @Transactional(readOnly = true)
    public Page<EventDto.Response> getEventsByDate(LocalDateTime date, String language, String type, 
                                                   String country, int page, int size, String sort, Long userId) {
        Pageable pageable = PageRequest.of(page, size);
        
        // Get user's preferred language
        String userLanguage = getUserLanguage(userId, language);
        
        // Get date range for the specific date
        LocalDateTime startOfDay = date.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        Page<Event> events = eventRepository.findByDate(startOfDay, endOfDay, pageable);
        return events.map(event -> convertToResponse(event, userLanguage));
    }

    /**
     * Get events by date range with pagination and filters
     */
    @Transactional(readOnly = true)
    public Page<EventDto.Response> getEventsByDateRange(LocalDateTime startDate, LocalDateTime endDate, 
                                                        String language, String type, String country, 
                                                        int page, int size, String sort, Long userId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> events = eventRepository.findByDateBetween(startDate, endDate, pageable);
        return events.map(event -> convertToResponse(event, language));
    }


    /**
     * Like an event
     */
    @Transactional
    public EventDto.LikeResponse likeEvent(Long eventId, Long userId) {
        // Check if already liked
        if (eventLikeRepository.existsByEventIdAndUserId(eventId, userId)) {
            return new EventDto.LikeResponse(false, false, 0);
        }

        // Get event
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return new EventDto.LikeResponse(false, false, 0);
        }

        Event event = eventOpt.get();

        // Create like
        EventLike like = new EventLike(event, userId);
        eventLikeRepository.save(like);

        // Update likes count
        event.setLikesCount(event.getLikesCount() + 1);
        eventRepository.save(event);

        return new EventDto.LikeResponse(true, true, event.getLikesCount());
    }

    /**
     * Unlike an event
     */
    @Transactional
    public EventDto.LikeResponse unlikeEvent(Long eventId, Long userId) {
        // Check if liked
        if (!eventLikeRepository.existsByEventIdAndUserId(eventId, userId)) {
            return new EventDto.LikeResponse(false, false, 0);
        }

        // Get event
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return new EventDto.LikeResponse(false, false, 0);
        }

        Event event = eventOpt.get();

        // Remove like
        eventLikeRepository.deleteByEventIdAndUserId(eventId, userId);

        // Update likes count
        event.setLikesCount(Math.max(0, event.getLikesCount() - 1));
        eventRepository.save(event);

        return new EventDto.LikeResponse(true, false, event.getLikesCount());
    }

    /**
     * Check if user has liked an event
     */
    @Transactional(readOnly = true)
    public boolean isEventLikedByUser(Long eventId, Long userId) {
        if (userId == null) {
            return false;
        }
        return eventLikeRepository.existsByEventIdAndUserId(eventId, userId);
    }

    /**
     * Convert Event entity to EventDto.Response
     */
    private EventDto.Response convertToResponse(Event event, String language) {
        // Get multilingual content
        String title = event.getTitleForLanguage(language);
        if (title == null) {
            title = event.getDefaultTitle();
        }

        String description = event.getDescriptionForLanguage(language);
        if (description == null) {
            description = event.getDefaultDescription();
        }

        String content = event.getContentForLanguage(language);
        if (content == null) {
            content = event.getDefaultContent();
        }

        // Get references
        List<EventReference> references = eventReferenceRepository.findByEventIdOrderByCreatedAtAsc(event.getId());
        List<EventDto.ReferenceDto> referenceDtos = references.stream()
                .map(ref -> new EventDto.ReferenceDto(ref.getTitle(), ref.getUrl()))
                .collect(Collectors.toList());

        return new EventDto.Response(
                event.getId().toString(),
                title,
                description,
                content,
                event.getDate().format(DATE_FORMATTER),
                event.getType(),
                event.getCountry(),
                event.getImageUrl(),
                event.getVideoUrls(),
                event.getAudioUrls(),
                referenceDtos,
                event.getLikesCount(),
                event.getCommentsCount()
        );
    }

    /**
     * Get a single event by ID
     */
    @Transactional(readOnly = true)
    public Optional<EventDto.Response> getEventById(Long eventId, String language, Long userId) {
        // Get user's preferred language
        String userLanguage = getUserLanguage(userId, language);
        
        // Use CacheWrapper for event detail caching
        String cacheKey = "event:" + eventId + ":" + userLanguage;
        
        EventDto.Response cachedEvent = cacheWrapper.get("eventDetail", cacheKey, EventDto.Response.class);
        if (cachedEvent != null) {
            return Optional.of(cachedEvent);
        }
        
        // Cache miss - fetch from database
        Optional<EventDto.Response> result = eventRepository.findById(eventId)
                .map(event -> convertToResponse(event, userLanguage));
        
        // Cache the result if present
        if (result.isPresent()) {
            cacheWrapper.put("eventDetail", cacheKey, result.get());
        }
        
        return result;
    }

    /**
     * Get similar events based on type and country
     */
    @Transactional(readOnly = true)
    public List<EventDto.Response> getSimilarEvents(Long eventId, String language, Long userId, int limit) {
        // Get user's preferred language
        String userLanguage = getUserLanguage(userId, language);
        
        // Get the original event to find similar ones
        Optional<Event> originalEventOpt = eventRepository.findById(eventId);
        if (originalEventOpt.isEmpty()) {
            return List.of();
        }
        
        Event originalEvent = originalEventOpt.get();
        Pageable pageable = PageRequest.of(0, limit);
        
        // Find events with same type and country, excluding the original event
        Page<Event> similarEvents = eventRepository.findByTypeAndCountryOrderByDateDesc(
            originalEvent.getType(), 
            originalEvent.getCountry(), 
            pageable
        );
        
        return similarEvents.getContent().stream()
                .filter(event -> !event.getId().equals(eventId))
                .map(event -> convertToResponse(event, userLanguage))
                .collect(Collectors.toList());
    }

    /**
     * Get trending events in the last 24 hours
     */
    @Transactional(readOnly = true)
    public Page<EventDto.Response> getTrendingEvents(String language, int page, int size, Long userId) {
        // Get user's preferred language
        String userLanguage = getUserLanguage(userId, language);
        
        // Get events from last 24 hours, ordered by likes count
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        Pageable pageable = PageRequest.of(page, size);
        
        Page<Event> trendingEvents = eventRepository.findByDateAfterOrderByLikesCountDesc(
            twentyFourHoursAgo, 
            pageable
        );
        
        return trendingEvents.map(event -> convertToResponse(event, userLanguage));
    }

    /**
     * Get today's events by country using optimized month_day query
     * This method uses the new performance indexes for optimal query performance
     */
    @Transactional(readOnly = true)
    public Page<EventDto.Response> findTodayByCountry(String countryCode, String language, int page, int size, Long userId) {
        // Get user's preferred language
        String userLanguage = getUserLanguage(userId, language);
        
        // Generate month_day for today (MM-DD format)
        LocalDate today = LocalDate.now();
        String monthDay = String.format("%02d-%02d", today.getMonthValue(), today.getDayOfMonth());
        
        // Use optimized repository method with performance indexes
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> events = eventRepository.findByCountryAndMonthDayOrderByScoreDesc(countryCode, monthDay, pageable);
        
        return events.map(event -> convertToResponse(event, userLanguage));
    }

    /**
     * Get tomorrow's events by country using optimized month_day query
     * This method uses the new performance indexes for optimal query performance
     */
    @Transactional(readOnly = true)
    public Page<EventDto.Response> findTomorrowByCountry(String countryCode, String language, int page, int size, Long userId) {
        // Get user's preferred language
        String userLanguage = getUserLanguage(userId, language);
        
        // Generate month_day for tomorrow (MM-DD format)
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String monthDay = String.format("%02d-%02d", tomorrow.getMonthValue(), tomorrow.getDayOfMonth());
        
        // Use optimized repository method with performance indexes
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> events = eventRepository.findByCountryAndMonthDayOrderByScoreDesc(countryCode, monthDay, pageable);
        
        return events.map(event -> convertToResponse(event, userLanguage));
    }

    /**
     * Get yesterday's events by country using optimized month_day query
     * This method uses the new performance indexes for optimal query performance
     */
    @Transactional(readOnly = true)
    public Page<EventDto.Response> findYesterdayByCountry(String countryCode, String language, int page, int size, Long userId) {
        // Get user's preferred language
        String userLanguage = getUserLanguage(userId, language);
        
        // Generate month_day for yesterday (MM-DD format)
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String monthDay = String.format("%02d-%02d", yesterday.getMonthValue(), yesterday.getDayOfMonth());
        
        // Use optimized repository method with performance indexes
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> events = eventRepository.findByCountryAndMonthDayOrderByScoreDesc(countryCode, monthDay, pageable);
        
        return events.map(event -> convertToResponse(event, userLanguage));
    }
}