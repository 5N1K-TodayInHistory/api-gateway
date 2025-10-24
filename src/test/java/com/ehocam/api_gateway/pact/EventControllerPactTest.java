package com.ehocam.api_gateway.pact;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.ehocam.api_gateway.dto.EventDto;
import com.ehocam.api_gateway.service.EventService;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;

@ExtendWith(PactVerificationInvocationContextProvider.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringJUnitConfig
@Provider("5n1k-api-gateway")
    @PactBroker(
        host = "localhost",
        port = "9292",
        authentication = @PactBrokerAuth(token = "${PACT_BROKER_TOKEN:}"),
        enablePendingPacts = "false"
    )
@org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable(
    named = "ENABLE_PACT_TESTS", 
    matches = "true"
)
public class EventControllerPactTest {

    @LocalServerPort
    private int port;

    @MockBean
    private EventService eventService;

    @BeforeEach
    void setUp(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @TestTemplate
    void pactVerificationTestTemplate(PactVerificationContext context) {
        try {
            context.verifyInteraction();
        } catch (Exception e) {
            // If no pacts are found, skip the test
            String errorMessage = e.getMessage();
            String className = e.getClass().getSimpleName();
            String fullClassName = e.getClass().getName();
            
            System.out.println("Exception caught: " + fullClassName);
            System.out.println("Exception message: " + errorMessage);
            
            if (errorMessage != null && (
                errorMessage.contains("No HAL document found") || 
                errorMessage.contains("No pacts found") ||
                errorMessage.contains("NotFoundHalResponse") ||
                errorMessage.contains("No HAL document found at path"))) {
                System.out.println("No pacts found in Pact Broker - skipping verification");
                return;
            }
            if (className.equals("NotFoundHalResponse") || fullClassName.contains("NotFoundHalResponse")) {
                System.out.println("No pacts found in Pact Broker - skipping verification");
                return;
            }
            throw e;
        }
    }

    @State("events exist for today")
    void eventsExistForToday() {
        // Setup test data for today's events
        EventDto.Response event1 = new EventDto.Response(
                "1",
                "Test Event 1",
                "Test Description 1",
                "Test Content 1",
                LocalDateTime.now().toString(),
                "politics",
                "TR",
                "https://example.com/image1.jpg",
                List.of("https://example.com/video1.mp4"),
                List.of("https://example.com/audio1.mp3"),
                List.of(),
                100,
                50
        );

        EventDto.Response event2 = new EventDto.Response(
                "2",
                "Test Event 2",
                "Test Description 2",
                "Test Content 2",
                LocalDateTime.now().toString(),
                "science",
                "TR",
                "https://example.com/image2.jpg",
                List.of("https://example.com/video2.mp4"),
                List.of("https://example.com/audio2.mp3"),
                List.of(),
                150,
                75
        );

        Page<EventDto.Response> mockPage = new PageImpl<>(
                List.of(event1, event2),
                PageRequest.of(0, 20),
                2
        );

        when(eventService.findTodayByCountry(anyString(), anyString(), anyInt(), anyInt(), anyLong()))
                .thenReturn(mockPage);
        when(eventService.getEventsForDay(anyInt(), any(), any(), anyInt(), anyInt(), anyString(), anyLong(), anyString()))
                .thenReturn(mockPage);
    }

    @State("events exist for tomorrow")
    void eventsExistForTomorrow() {
        // Setup test data for tomorrow's events
        EventDto.Response event1 = new EventDto.Response(
                "3",
                "Tomorrow Event 1",
                "Tomorrow Description 1",
                "Tomorrow Content 1",
                LocalDateTime.now().plusDays(1).toString(),
                "sports",
                "TR",
                "https://example.com/image3.jpg",
                List.of("https://example.com/video3.mp4"),
                List.of("https://example.com/audio3.mp3"),
                List.of(),
                200,
                100
        );

        Page<EventDto.Response> mockPage = new PageImpl<>(
                List.of(event1),
                PageRequest.of(0, 20),
                1
        );

        when(eventService.findTomorrowByCountry(anyString(), anyString(), anyInt(), anyInt(), anyLong()))
                .thenReturn(mockPage);
        when(eventService.getEventsForDay(anyInt(), any(), any(), anyInt(), anyInt(), anyString(), anyLong(), anyString()))
                .thenReturn(mockPage);
    }

    @State("events exist for yesterday")
    void eventsExistForYesterday() {
        // Setup test data for yesterday's events
        EventDto.Response event1 = new EventDto.Response(
                "4",
                "Yesterday Event 1",
                "Yesterday Description 1",
                "Yesterday Content 1",
                LocalDateTime.now().minusDays(1).toString(),
                "history",
                "TR",
                "https://example.com/image4.jpg",
                List.of("https://example.com/video4.mp4"),
                List.of("https://example.com/audio4.mp3"),
                List.of(),
                75,
                25
        );

        Page<EventDto.Response> mockPage = new PageImpl<>(
                List.of(event1),
                PageRequest.of(0, 20),
                1
        );

        when(eventService.findYesterdayByCountry(anyString(), anyString(), anyInt(), anyInt(), anyLong()))
                .thenReturn(mockPage);
        when(eventService.getEventsForDay(anyInt(), any(), any(), anyInt(), anyInt(), anyString(), anyLong(), anyString()))
                .thenReturn(mockPage);
    }

    @State("no events exist")
    void noEventsExist() {
        // Setup empty response
        Page<EventDto.Response> emptyPage = new PageImpl<>(
                List.of(),
                PageRequest.of(0, 20),
                0
        );

        when(eventService.findTodayByCountry(anyString(), anyString(), anyInt(), anyInt(), anyLong()))
                .thenReturn(emptyPage);
        when(eventService.getEventsForDay(anyInt(), any(), any(), anyInt(), anyInt(), anyString(), anyLong(), anyString()))
                .thenReturn(emptyPage);
    }

    @State("event with specific ID exists")
    void eventWithSpecificIdExists() {
        // Setup test data for specific event
        EventDto.Response event = new EventDto.Response(
                "5",
                "Specific Event",
                "Specific Description",
                "Specific Content",
                LocalDateTime.now().toString(),
                "entertainment",
                "US",
                "https://example.com/image5.jpg",
                List.of("https://example.com/video5.mp4"),
                List.of("https://example.com/audio5.mp3"),
                List.of(),
                300,
                150
        );

        when(eventService.getEventById(5L, "en", null))
                .thenReturn(java.util.Optional.of(event));
    }

    @State("event with specific ID does not exist")
    void eventWithSpecificIdDoesNotExist() {
        // Setup empty response for non-existent event
        when(eventService.getEventById(999L, "en", null))
                .thenReturn(java.util.Optional.empty());
    }
}
