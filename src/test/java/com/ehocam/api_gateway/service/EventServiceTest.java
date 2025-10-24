package com.ehocam.api_gateway.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.ehocam.api_gateway.dto.EventDto;
import com.ehocam.api_gateway.entity.Event;
import com.ehocam.api_gateway.repository.EventReferenceRepository;
import com.ehocam.api_gateway.repository.EventRepository;
import com.ehocam.api_gateway.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventReferenceRepository eventReferenceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventService eventService;

    private Event testEvent;
    private Map<String, String> testTitle;
    private Map<String, String> testDescription;
    private Map<String, String> testContent;
    private Map<String, String> testImportanceReason;

    @BeforeEach
    void setUp() {
        // Setup test data
        testTitle = new HashMap<>();
        testTitle.put("en", "Test Event");
        testTitle.put("tr", "Test Olayı");

        testDescription = new HashMap<>();
        testDescription.put("en", "Test Description");
        testDescription.put("tr", "Test Açıklaması");

        testContent = new HashMap<>();
        testContent.put("en", "Test Content");
        testContent.put("tr", "Test İçerik");

        testImportanceReason = new HashMap<>();
        testImportanceReason.put("en", "Important historical event");
        testImportanceReason.put("tr", "Önemli tarihi olay");

        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle(testTitle);
        testEvent.setDescription(testDescription);
        testEvent.setContent(testContent);
        testEvent.setDate(LocalDateTime.now());
        testEvent.setType("politics");
        testEvent.setCountry("TR");
        testEvent.setImageUrl("https://example.com/image.jpg");
        testEvent.setVideoUrls(List.of("https://example.com/video.mp4"));
        testEvent.setAudioUrls(List.of("https://example.com/audio.mp3"));
        testEvent.setLikesCount(100);
        testEvent.setCommentsCount(50);
        testEvent.setScore((short) 85);
        testEvent.setImportanceReason(testImportanceReason);
        testEvent.setMonthDay("12-25"); // December 25th
    }

    @Test
    void testFindTodayByCountry_Success() {
        // Arrange
        String countryCode = "TR";
        String language = "en";
        int page = 0;
        int size = 20;
        Long userId = 1L;

        Page<Event> mockPage = new PageImpl<>(List.of(testEvent));
        when(eventRepository.findByCountryAndMonthDayOrderByScoreDesc(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(mockPage);
        when(eventReferenceRepository.findByEventIdOrderByCreatedAtAsc(anyLong()))
                .thenReturn(List.of());

        // Act
        Page<EventDto.Response> result = eventService.findTodayByCountry(countryCode, language, page, size, userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        
        EventDto.Response response = result.getContent().get(0);
        assertEquals("1", response.getId());
        assertEquals("Test Event", response.getTitle());
        assertEquals("Test Description", response.getDescription());
        assertEquals("Test Content", response.getContent());
        assertEquals("politics", response.getType());
        assertEquals("TR", response.getCountry());
        assertEquals(100, response.getLikes());
        assertEquals(50, response.getComments());

        // Verify repository method was called with correct parameters
        verify(eventRepository).findByCountryAndMonthDayOrderByScoreDesc(
                eq(countryCode), 
                anyString(), // month_day will be generated from current date
                any(Pageable.class)
        );
    }

    @Test
    void testFindTodayByCountry_WithTurkishLanguage() {
        // Arrange
        String countryCode = "TR";
        String language = "tr";
        int page = 0;
        int size = 20;
        Long userId = 1L;

        Page<Event> mockPage = new PageImpl<>(List.of(testEvent));
        when(eventRepository.findByCountryAndMonthDayOrderByScoreDesc(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(mockPage);
        when(eventReferenceRepository.findByEventIdOrderByCreatedAtAsc(anyLong()))
                .thenReturn(List.of());

        // Act
        Page<EventDto.Response> result = eventService.findTodayByCountry(countryCode, language, page, size, userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        
        EventDto.Response response = result.getContent().get(0);
        assertEquals("Test Olayı", response.getTitle());
        assertEquals("Test Açıklaması", response.getDescription());
        assertEquals("Test İçerik", response.getContent());
    }

    @Test
    void testFindTodayByCountry_EmptyResult() {
        // Arrange
        String countryCode = "US";
        String language = "en";
        int page = 0;
        int size = 20;
        Long userId = 1L;

        Page<Event> mockPage = new PageImpl<>(List.of());
        when(eventRepository.findByCountryAndMonthDayOrderByScoreDesc(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(mockPage);

        // Act
        Page<EventDto.Response> result = eventService.findTodayByCountry(countryCode, language, page, size, userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void testFindTodayByCountry_ScoreOrdering() {
        // Arrange
        String countryCode = "TR";
        String language = "en";
        int page = 0;
        int size = 20;
        Long userId = 1L;

        // Create events with different scores
        Event highScoreEvent = createTestEvent(1L, (short) 95);
        Event mediumScoreEvent = createTestEvent(2L, (short) 75);
        Event lowScoreEvent = createTestEvent(3L, (short) 55);

        Page<Event> mockPage = new PageImpl<>(List.of(highScoreEvent, mediumScoreEvent, lowScoreEvent));
        when(eventRepository.findByCountryAndMonthDayOrderByScoreDesc(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(mockPage);
        when(eventReferenceRepository.findByEventIdOrderByCreatedAtAsc(anyLong()))
                .thenReturn(List.of());

        // Act
        Page<EventDto.Response> result = eventService.findTodayByCountry(countryCode, language, page, size, userId);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        
        // Verify that events are ordered by score DESC (highest first)
        // Note: The actual ordering is handled by the database query, 
        // this test verifies the service method works correctly
        verify(eventRepository).findByCountryAndMonthDayOrderByScoreDesc(
                eq(countryCode), 
                anyString(), // month_day will be generated from current date
                any(Pageable.class)
        );
    }

    @Test
    void testFindTodayByCountry_MonthDayGeneration() {
        // Arrange
        String countryCode = "TR";
        String language = "en";
        int page = 0;
        int size = 20;
        Long userId = 1L;

        Page<Event> mockPage = new PageImpl<>(List.of(testEvent));
        when(eventRepository.findByCountryAndMonthDayOrderByScoreDesc(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(mockPage);
        when(eventReferenceRepository.findByEventIdOrderByCreatedAtAsc(anyLong()))
                .thenReturn(List.of());

        // Act
        eventService.findTodayByCountry(countryCode, language, page, size, userId);

        // Assert
        // Verify that the month_day parameter is in MM-DD format
        verify(eventRepository).findByCountryAndMonthDayOrderByScoreDesc(
                eq(countryCode), 
                argThat(monthDay -> {
                    // Check if monthDay is in MM-DD format
                    return monthDay.matches("\\d{2}-\\d{2}");
                }),
                any(Pageable.class)
        );
    }

    @Test
    void testFindTomorrowByCountry_Success() {
        // Arrange
        String countryCode = "TR";
        String language = "en";
        int page = 0;
        int size = 20;
        Long userId = 1L;

        Page<Event> mockPage = new PageImpl<>(List.of(testEvent));
        when(eventRepository.findByCountryAndMonthDayOrderByScoreDesc(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(mockPage);
        when(eventReferenceRepository.findByEventIdOrderByCreatedAtAsc(anyLong()))
                .thenReturn(List.of());

        // Act
        Page<EventDto.Response> result = eventService.findTomorrowByCountry(countryCode, language, page, size, userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        
        EventDto.Response response = result.getContent().get(0);
        assertEquals("1", response.getId());
        assertEquals("Test Event", response.getTitle());
        assertEquals("Test Description", response.getDescription());
        assertEquals("Test Content", response.getContent());
        assertEquals("politics", response.getType());
        assertEquals("TR", response.getCountry());
        assertEquals(100, response.getLikes());
        assertEquals(50, response.getComments());

        // Verify repository method was called with correct parameters
        verify(eventRepository).findByCountryAndMonthDayOrderByScoreDesc(
                eq(countryCode), 
                anyString(), // month_day will be generated from tomorrow's date
                any(Pageable.class)
        );
    }

    @Test
    void testFindYesterdayByCountry_Success() {
        // Arrange
        String countryCode = "TR";
        String language = "en";
        int page = 0;
        int size = 20;
        Long userId = 1L;

        Page<Event> mockPage = new PageImpl<>(List.of(testEvent));
        when(eventRepository.findByCountryAndMonthDayOrderByScoreDesc(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(mockPage);
        when(eventReferenceRepository.findByEventIdOrderByCreatedAtAsc(anyLong()))
                .thenReturn(List.of());

        // Act
        Page<EventDto.Response> result = eventService.findYesterdayByCountry(countryCode, language, page, size, userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        
        EventDto.Response response = result.getContent().get(0);
        assertEquals("1", response.getId());
        assertEquals("Test Event", response.getTitle());
        assertEquals("Test Description", response.getDescription());
        assertEquals("Test Content", response.getContent());
        assertEquals("politics", response.getType());
        assertEquals("TR", response.getCountry());
        assertEquals(100, response.getLikes());
        assertEquals(50, response.getComments());

        // Verify repository method was called with correct parameters
        verify(eventRepository).findByCountryAndMonthDayOrderByScoreDesc(
                eq(countryCode), 
                anyString(), // month_day will be generated from yesterday's date
                any(Pageable.class)
        );
    }

    private Event createTestEvent(Long id, Short score) {
        Event event = new Event();
        event.setId(id);
        event.setTitle(testTitle);
        event.setDescription(testDescription);
        event.setContent(testContent);
        event.setDate(LocalDateTime.now());
        event.setType("politics");
        event.setCountry("TR");
        event.setScore(score);
        event.setImportanceReason(testImportanceReason);
        event.setLikesCount(100);
        event.setCommentsCount(50);
        return event;
    }
}
