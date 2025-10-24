package com.ehocam.api_gateway.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.ehocam.api_gateway.dto.EventDto;
import com.ehocam.api_gateway.service.EventService;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    private EventDto.Response testEventResponse;

    @BeforeEach
    void setUp() {
        testEventResponse = new EventDto.Response(
                "1",
                "Test Event",
                "Test Description",
                "Test Content",
                LocalDateTime.now().toString(),
                "politics",
                "TR",
                "https://example.com/image.jpg",
                List.of("https://example.com/video.mp4"),
                List.of("https://example.com/audio.mp3"),
                List.of(),
                100,
                50
        );
    }

    @Test
    @WithMockUser(username = "1")
    void testGetTodaysEvents_WithCountry_OptimizedPath() throws Exception {
        // Arrange
        Page<EventDto.Response> mockPage = new PageImpl<>(List.of(testEventResponse));
        when(eventService.findTodayByCountry(anyString(), anyString(), anyInt(), anyInt(), anyLong()))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/api/events/today")
                .param("country", "TR")
                .param("lang", "en")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].id").value("1"))
                .andExpect(jsonPath("$.data.content[0].title").value("Test Event"))
                .andExpect(jsonPath("$.data.content[0].country").value("TR"));

        // Verify that the optimized method was called
        verify(eventService).findTodayByCountry(eq("TR"), eq("en"), eq(0), eq(20), eq(1L));
    }

    @Test
    @WithMockUser(username = "1")
    void testGetTodaysEvents_WithoutCountry_StandardPath() throws Exception {
        // Arrange
        Page<EventDto.Response> mockPage = new PageImpl<>(List.of(testEventResponse));
        when(eventService.getEventsForDay(anyInt(), any(), any(), anyInt(), anyInt(), anyString(), anyLong(), anyString()))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/api/events/today")
                .param("lang", "en")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());

        // Verify that the standard method was called (not the optimized one)
        verify(eventService).getEventsForDay(eq(0), isNull(), isNull(), eq(0), eq(20), anyString(), eq(1L), eq("en"));
        verify(eventService, never()).findTodayByCountry(anyString(), anyString(), anyInt(), anyInt(), anyLong());
    }

    @Test
    @WithMockUser(username = "1")
    void testGetTodaysEvents_WithTurkishLanguage() throws Exception {
        // Arrange
        EventDto.Response turkishEventResponse = new EventDto.Response(
                "1",
                "Test Olayı",
                "Test Açıklaması",
                "Test İçerik",
                LocalDateTime.now().toString(),
                "politics",
                "TR",
                "https://example.com/image.jpg",
                List.of("https://example.com/video.mp4"),
                List.of("https://example.com/audio.mp3"),
                List.of(),
                100,
                50
        );

        Page<EventDto.Response> mockPage = new PageImpl<>(List.of(turkishEventResponse));
        when(eventService.findTodayByCountry(anyString(), anyString(), anyInt(), anyInt(), anyLong()))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/api/events/today")
                .param("country", "TR")
                .param("lang", "tr")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Test Olayı"))
                .andExpect(jsonPath("$.data.content[0].description").value("Test Açıklaması"));

        verify(eventService).findTodayByCountry(eq("TR"), eq("tr"), eq(0), eq(20), eq(1L));
    }

    @Test
    @WithMockUser(username = "1")
    void testGetTodaysEvents_WithPagination() throws Exception {
        // Arrange
        Page<EventDto.Response> mockPage = new PageImpl<>(List.of(testEventResponse), PageRequest.of(1, 5), 25);
        when(eventService.findTodayByCountry(anyString(), anyString(), anyInt(), anyInt(), anyLong()))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/api/events/today")
                .param("country", "TR")
                .param("lang", "en")
                .param("page", "1")
                .param("size", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(25))
                .andExpect(jsonPath("$.data.totalPages").value(5))
                .andExpect(jsonPath("$.data.number").value(1))
                .andExpect(jsonPath("$.data.size").value(5));

        verify(eventService).findTodayByCountry(eq("TR"), eq("en"), eq(1), eq(5), eq(1L));
    }

    @Test
    void testGetTodaysEvents_WithoutAuthentication() throws Exception {
        // Arrange
        Page<EventDto.Response> mockPage = new PageImpl<>(List.of(testEventResponse));
        when(eventService.findTodayByCountry(anyString(), anyString(), anyInt(), anyInt(), isNull()))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/api/events/today")
                .param("country", "TR")
                .param("lang", "en")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(eventService).findTodayByCountry(eq("TR"), eq("en"), eq(0), eq(20), isNull());
    }

    @Test
    @WithMockUser(username = "1")
    void testGetTodaysEvents_WithEmptyCountry_StandardPath() throws Exception {
        // Arrange
        Page<EventDto.Response> mockPage = new PageImpl<>(List.of(testEventResponse));
        when(eventService.getEventsForDay(anyInt(), any(), any(), anyInt(), anyInt(), anyString(), anyLong(), anyString()))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/api/events/today")
                .param("country", "")
                .param("lang", "en")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Verify that the standard method was called (not the optimized one)
        verify(eventService).getEventsForDay(eq(0), isNull(), eq(""), eq(0), eq(20), anyString(), eq(1L), eq("en"));
        verify(eventService, never()).findTodayByCountry(anyString(), anyString(), anyInt(), anyInt(), anyLong());
    }

    @Test
    @WithMockUser(username = "1")
    void testGetTodaysEvents_WithTypeFilter_StandardPath() throws Exception {
        // Arrange
        Page<EventDto.Response> mockPage = new PageImpl<>(List.of(testEventResponse));
        when(eventService.getEventsForDay(anyInt(), any(), any(), anyInt(), anyInt(), anyString(), anyLong(), anyString()))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/api/events/today")
                .param("country", "TR")
                .param("type", "politics")
                .param("lang", "en")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // When type filter is present, it should use standard method even with country
        verify(eventService).getEventsForDay(eq(0), eq("politics"), eq("TR"), eq(0), eq(20), anyString(), eq(1L), eq("en"));
        verify(eventService, never()).findTodayByCountry(anyString(), anyString(), anyInt(), anyInt(), anyLong());
    }

    @Test
    @WithMockUser(username = "1")
    void testGetTomorrowsEvents_WithCountry_OptimizedPath() throws Exception {
        // Arrange
        Page<EventDto.Response> mockPage = new PageImpl<>(List.of(testEventResponse));
        when(eventService.findTomorrowByCountry(anyString(), anyString(), anyInt(), anyInt(), anyLong()))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/api/events/tomorrow")
                .param("country", "TR")
                .param("lang", "en")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].id").value("1"))
                .andExpect(jsonPath("$.data.content[0].title").value("Test Event"))
                .andExpect(jsonPath("$.data.content[0].country").value("TR"));

        // Verify that the optimized method was called
        verify(eventService).findTomorrowByCountry(eq("TR"), eq("en"), eq(0), eq(20), eq(1L));
    }

    @Test
    @WithMockUser(username = "1")
    void testGetYesterdaysEvents_WithCountry_OptimizedPath() throws Exception {
        // Arrange
        Page<EventDto.Response> mockPage = new PageImpl<>(List.of(testEventResponse));
        when(eventService.findYesterdayByCountry(anyString(), anyString(), anyInt(), anyInt(), anyLong()))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/api/events/yesterday")
                .param("country", "TR")
                .param("lang", "en")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].id").value("1"))
                .andExpect(jsonPath("$.data.content[0].title").value("Test Event"))
                .andExpect(jsonPath("$.data.content[0].country").value("TR"));

        // Verify that the optimized method was called
        verify(eventService).findYesterdayByCountry(eq("TR"), eq("en"), eq(0), eq(20), eq(1L));
    }

    @Test
    @WithMockUser(username = "1")
    void testGetTomorrowsEvents_WithoutCountry_StandardPath() throws Exception {
        // Arrange
        Page<EventDto.Response> mockPage = new PageImpl<>(List.of(testEventResponse));
        when(eventService.getEventsForDay(anyInt(), any(), any(), anyInt(), anyInt(), anyString(), anyLong(), anyString()))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/api/events/tomorrow")
                .param("lang", "en")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Verify that the standard method was called (not the optimized one)
        verify(eventService).getEventsForDay(eq(1), isNull(), isNull(), eq(0), eq(20), anyString(), eq(1L), eq("en"));
        verify(eventService, never()).findTomorrowByCountry(anyString(), anyString(), anyInt(), anyInt(), anyLong());
    }

    @Test
    @WithMockUser(username = "1")
    void testGetYesterdaysEvents_WithoutCountry_StandardPath() throws Exception {
        // Arrange
        Page<EventDto.Response> mockPage = new PageImpl<>(List.of(testEventResponse));
        when(eventService.getEventsForDay(anyInt(), any(), any(), anyInt(), anyInt(), anyString(), anyLong(), anyString()))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/api/events/yesterday")
                .param("lang", "en")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Verify that the standard method was called (not the optimized one)
        verify(eventService).getEventsForDay(eq(-1), isNull(), isNull(), eq(0), eq(20), anyString(), eq(1L), eq("en"));
        verify(eventService, never()).findYesterdayByCountry(anyString(), anyString(), anyInt(), anyInt(), anyLong());
    }
}
