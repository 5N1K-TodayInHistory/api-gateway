package com.ehocam.api_gateway.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ehocam.api_gateway.dto.EventDto;
import com.ehocam.api_gateway.entity.Event;
import com.ehocam.api_gateway.entity.User;
import com.ehocam.api_gateway.repository.EventRepository;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private EventService eventService;

    private User testUser;
    private Event testEvent;

    @BeforeEach
    void setUp() {
        // Test user setup
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        
        // Set user preferences with Turkish language
        User.UserPreferences preferences = new User.UserPreferences();
        preferences.setLanguage("tr");
        testUser.setPreferences(preferences);

        // Test event setup with i18n content
        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("Original Title");
        testEvent.setSummary("Original Summary");
        testEvent.setContent("Original Content");
        testEvent.setDate(LocalDateTime.now());
        testEvent.setCategory(Event.Category.HISTORY);
        testEvent.setCountry(Event.Country.ALL);
        testEvent.setRatio(85);

        // Add i18n content
        Map<String, Event.I18nContent> i18n = new HashMap<>();
        i18n.put("tr", new Event.I18nContent("Türkçe Başlık", "Türkçe Özet", "Türkçe İçerik"));
        i18n.put("en", new Event.I18nContent("English Title", "English Summary", "English Content"));
        testEvent.setI18n(i18n);

        // Add media with i18n audio
        Event.Media media = new Event.Media();
        media.setAudioUrl("default-audio.mp3");
        Map<String, Event.MediaI18n> mediaI18n = new HashMap<>();
        mediaI18n.put("tr", new Event.MediaI18n("turkish-audio.mp3"));
        mediaI18n.put("en", new Event.MediaI18n("english-audio.mp3"));
        media.setI18n(mediaI18n);
        testEvent.setMedia(media);

        // Add engagement
        Event.Engagement engagement = new Event.Engagement();
        engagement.setLikes(5);
        engagement.setComments(2);
        engagement.setShares(1);
        testEvent.setEngagement(engagement);
    }

    @Test
    void testGetEventsForUser_ShouldReturnLocalizedContent() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventPage = new PageImpl<>(java.util.List.of(testEvent), pageable, 1);
        
        when(authService.getUserByUsername("testuser")).thenReturn(testUser);
        when(eventRepository.findAll(pageable)).thenReturn(eventPage);

        // Act
        Page<EventDto.Response> result = eventService.getEventsForUser("testuser", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        
        EventDto.Response response = result.getContent().get(0);
        assertEquals("Türkçe Başlık", response.getTitle());
        assertEquals("Türkçe Özet", response.getSummary());
        assertEquals("Türkçe İçerik", response.getContent());
        assertEquals("turkish-audio.mp3", response.getMedia().getAudioUrl());
        assertEquals(5, response.getEngagement().getLikes());
        assertEquals(85, response.getRatio());
    }

    @Test
    void testGetEventsForUser_ShouldFallbackToEnglish() {
        // Arrange
        User englishUser = new User();
        englishUser.setUsername("englishuser");
        User.UserPreferences preferences = new User.UserPreferences();
        preferences.setLanguage("en");
        englishUser.setPreferences(preferences);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventPage = new PageImpl<>(java.util.List.of(testEvent), pageable, 1);
        
        when(authService.getUserByUsername("englishuser")).thenReturn(englishUser);
        when(eventRepository.findAll(pageable)).thenReturn(eventPage);

        // Act
        Page<EventDto.Response> result = eventService.getEventsForUser("englishuser", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        
        EventDto.Response response = result.getContent().get(0);
        assertEquals("English Title", response.getTitle());
        assertEquals("English Summary", response.getSummary());
        assertEquals("English Content", response.getContent());
        assertEquals("english-audio.mp3", response.getMedia().getAudioUrl());
        assertEquals(85, response.getRatio());
    }

    @Test
    void testGetEventsForUser_ShouldFallbackToOriginalContent() {
        // Arrange
        User unknownLanguageUser = new User();
        unknownLanguageUser.setUsername("unknownuser");
        User.UserPreferences preferences = new User.UserPreferences();
        preferences.setLanguage("fr"); // French - not available
        unknownLanguageUser.setPreferences(preferences);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventPage = new PageImpl<>(java.util.List.of(testEvent), pageable, 1);
        
        when(authService.getUserByUsername("unknownuser")).thenReturn(unknownLanguageUser);
        when(eventRepository.findAll(pageable)).thenReturn(eventPage);

        // Act
        Page<EventDto.Response> result = eventService.getEventsForUser("unknownuser", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        
        EventDto.Response response = result.getContent().get(0);
        assertEquals("English Title", response.getTitle()); // Should fallback to English
        assertEquals("English Summary", response.getSummary());
        assertEquals("English Content", response.getContent());
        assertEquals("english-audio.mp3", response.getMedia().getAudioUrl());
        assertEquals(85, response.getRatio());
    }

    @Test
    void testGetEventsForUserByDate_ShouldReturnLocalizedContent() {
        // Arrange
        LocalDateTime targetDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventPage = new PageImpl<>(java.util.List.of(testEvent), pageable, 1);
        
        when(authService.getUserByUsername("testuser")).thenReturn(testUser);
        when(eventRepository.findByDate(targetDate, pageable)).thenReturn(eventPage);

        // Act
        Page<EventDto.Response> result = eventService.getEventsForUserByDate("testuser", targetDate, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        
        EventDto.Response response = result.getContent().get(0);
        assertEquals("Türkçe Başlık", response.getTitle());
        assertEquals("turkish-audio.mp3", response.getMedia().getAudioUrl());
        assertEquals(85, response.getRatio());
    }

    @Test
    void testGetEventsForUser_WithNoPreferences_ShouldUseDefaultLanguage() {
        // Arrange
        User noPreferencesUser = new User();
        noPreferencesUser.setUsername("noprefsuser");
        noPreferencesUser.setPreferences(null); // No preferences

        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventPage = new PageImpl<>(java.util.List.of(testEvent), pageable, 1);
        
        when(authService.getUserByUsername("noprefsuser")).thenReturn(noPreferencesUser);
        when(eventRepository.findAll(pageable)).thenReturn(eventPage);

        // Act
        Page<EventDto.Response> result = eventService.getEventsForUser("noprefsuser", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        
        EventDto.Response response = result.getContent().get(0);
        assertEquals("English Title", response.getTitle()); // Should use default English
        assertEquals("english-audio.mp3", response.getMedia().getAudioUrl());
        assertEquals(85, response.getRatio());
    }
}
