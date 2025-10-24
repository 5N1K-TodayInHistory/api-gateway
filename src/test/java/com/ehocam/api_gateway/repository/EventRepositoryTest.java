package com.ehocam.api_gateway.repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.ehocam.api_gateway.entity.Event;

@DataJpaTest
@ActiveProfiles("test")
class EventRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EventRepository eventRepository;

    @Test
    void testFindByCountryAndMonthDayOrderByScoreDesc_Success() {
        // Arrange
        String country = "TR";
        String monthDay = "12-25";
        
        // Create test events with different scores
        Event event1 = createTestEvent("Event 1", country, (short) 95);
        Event event2 = createTestEvent("Event 2", country, (short) 75);
        Event event3 = createTestEvent("Event 3", country, (short) 85);
        
        // Create event for different country (should not be returned)
        Event event4 = createTestEvent("Event 4", "US", (short) 90);
        
        // Create event for different month_day (should not be returned)
        Event event5 = createTestEvent("Event 5", country, (short) 80);

        // Persist all events
        entityManager.persistAndFlush(event1);
        entityManager.persistAndFlush(event2);
        entityManager.persistAndFlush(event3);
        entityManager.persistAndFlush(event4);
        entityManager.persistAndFlush(event5);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Event> result = eventRepository.findByCountryAndMonthDayOrderByScoreDesc(country, monthDay, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        
        // Verify ordering by score DESC
        List<Event> events = result.getContent();
        assertEquals((short) 95, events.get(0).getScore()); // Highest score first
        assertEquals((short) 85, events.get(1).getScore()); // Second highest
        assertEquals((short) 75, events.get(2).getScore()); // Lowest score last
        
        // Verify all events belong to the correct country and month_day
        events.forEach(event -> {
            assertEquals(country, event.getCountry());
            assertEquals(monthDay, event.getMonthDay());
        });
    }

    @Test
    void testFindByCountryAndMonthDayOrderByScoreDesc_EmptyResult() {
        // Arrange
        String country = "FR";
        String monthDay = "12-25";
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Event> result = eventRepository.findByCountryAndMonthDayOrderByScoreDesc(country, monthDay, pageable);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void testFindByCountryAndMonthDayOrderByScoreDesc_WithNullScores() {
        // Arrange
        String country = "TR";
        String monthDay = "12-25";
        
        // Create events with null scores (should be ordered last)
        Event event1 = createTestEvent("Event 1", country, (short) 90);
        Event event2 = createTestEvent("Event 2", country, null);
        Event event3 = createTestEvent("Event 3", country, (short) 80);

        entityManager.persistAndFlush(event1);
        entityManager.persistAndFlush(event2);
        entityManager.persistAndFlush(event3);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Event> result = eventRepository.findByCountryAndMonthDayOrderByScoreDesc(country, monthDay, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        
        // Verify ordering: non-null scores first (DESC), then null scores
        List<Event> events = result.getContent();
        assertEquals((short) 90, events.get(0).getScore());
        assertEquals((short) 80, events.get(1).getScore());
        assertNull(events.get(2).getScore()); // Null score should be last
    }

    @Test
    void testFindByCountryAndMonthDayOrderByScoreDesc_Pagination() {
        // Arrange
        String country = "TR";
        String monthDay = "12-25";
        
        // Create 5 events
        for (int i = 0; i < 5; i++) {
            Event event = createTestEvent("Event " + i, country, (short) (100 - i));
            entityManager.persistAndFlush(event);
        }

        // Test first page
        Pageable firstPage = PageRequest.of(0, 2);
        Page<Event> firstPageResult = eventRepository.findByCountryAndMonthDayOrderByScoreDesc(country, monthDay, firstPage);
        
        // Test second page
        Pageable secondPage = PageRequest.of(1, 2);
        Page<Event> secondPageResult = eventRepository.findByCountryAndMonthDayOrderByScoreDesc(country, monthDay, secondPage);

        // Assert
        assertEquals(2, firstPageResult.getContent().size());
        assertEquals(2, secondPageResult.getContent().size());
        assertEquals(5, firstPageResult.getTotalElements());
        assertEquals(3, firstPageResult.getTotalPages());
        
        // Verify ordering is maintained across pages
        List<Event> firstPageEvents = firstPageResult.getContent();
        List<Event> secondPageEvents = secondPageResult.getContent();
        
        assertEquals((short) 100, firstPageEvents.get(0).getScore());
        assertEquals((short) 99, firstPageEvents.get(1).getScore());
        assertEquals((short) 98, secondPageEvents.get(0).getScore());
        assertEquals((short) 97, secondPageEvents.get(1).getScore());
    }

    private Event createTestEvent(String title, String country, Short score) {
        Map<String, String> titleMap = new HashMap<>();
        titleMap.put("en", title);
        titleMap.put("tr", title + " (TR)");

        Map<String, String> descriptionMap = new HashMap<>();
        descriptionMap.put("en", "Description for " + title);
        descriptionMap.put("tr", title + " için açıklama");

        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("en", "Content for " + title);
        contentMap.put("tr", title + " için içerik");

        Map<String, String> importanceReasonMap = new HashMap<>();
        importanceReasonMap.put("en", "Important event");
        importanceReasonMap.put("tr", "Önemli olay");

        Event event = new Event();
        event.setTitle(titleMap);
        event.setDescription(descriptionMap);
        event.setContent(contentMap);
        event.setDate(LocalDateTime.now());
        event.setType("politics");
        event.setCountry(country);
        event.setScore(score);
        event.setImportanceReason(importanceReasonMap);
        event.setLikesCount(100);
        event.setCommentsCount(50);
        // month_day is a generated column, so we don't set it directly
        // It will be generated from the date field
        
        return event;
    }
}
