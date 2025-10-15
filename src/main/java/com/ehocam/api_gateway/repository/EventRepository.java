package com.ehocam.api_gateway.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ehocam.api_gateway.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Full-text search using PostgreSQL's to_tsvector and plainto_tsquery
    @Query(value = """
        SELECT * FROM events 
        WHERE (:q IS NULL OR to_tsvector('english', title || ' ' || summary || ' ' || content) 
              @@ plainto_tsquery('english', :q))
        AND (:date IS NULL OR DATE(date) = DATE(:date))
        AND (:country IS NULL OR country = :country)
        AND (:category IS NULL OR category = :category)
        ORDER BY 
            ratio DESC,
            CASE WHEN :sort = 'DATE_DESC' THEN date END DESC,
            CASE WHEN :sort = 'RECENT' THEN created_at END DESC,
            CASE WHEN :sort = 'POPULARITY' THEN (COALESCE((engagement->>'likes')::bigint, 0) + COALESCE((engagement->>'comments')::bigint, 0) + COALESCE((engagement->>'shares')::bigint, 0)) END DESC,
            created_at DESC
        """, nativeQuery = true, countQuery = """
        SELECT COUNT(*) FROM events 
        WHERE (:q IS NULL OR to_tsvector('english', title || ' ' || summary || ' ' || content) 
              @@ plainto_tsquery('english', :q))
        AND (:date IS NULL OR DATE(date) = DATE(:date))
        AND (:country IS NULL OR country = :country)
        AND (:category IS NULL OR category = :category)
        """)
    Page<Event> findEventsWithFilters(
        @Param("q") String query,
        @Param("date") LocalDateTime date,
        @Param("country") Event.Country country,
        @Param("category") Event.Category category,
        @Param("sort") String sort,
        Pageable pageable
    );

    // Alternative search using ILIKE for case-insensitive partial matching
    @Query(value = """
        SELECT * FROM events 
        WHERE (:q IS NULL OR (
            LOWER(title) LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(summary) LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(content) LIKE LOWER(CONCAT('%', :q, '%'))
        ))
        AND (:date IS NULL OR DATE(date) = DATE(:date))
        AND (:country IS NULL OR country = :country)
        AND (:category IS NULL OR category = :category)
        ORDER BY 
            ratio DESC,
            CASE WHEN :sort = 'DATE_DESC' THEN date END DESC,
            CASE WHEN :sort = 'RECENT' THEN created_at END DESC,
            CASE WHEN :sort = 'POPULARITY' THEN (COALESCE((engagement->>'likes')::bigint, 0) + COALESCE((engagement->>'comments')::bigint, 0) + COALESCE((engagement->>'shares')::bigint, 0)) END DESC,
            created_at DESC
        """, nativeQuery = true, countQuery = """
        SELECT COUNT(*) FROM events 
        WHERE (:q IS NULL OR (
            LOWER(title) LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(summary) LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(content) LIKE LOWER(CONCAT('%', :q, '%'))
        ))
        AND (:date IS NULL OR DATE(date) = DATE(:date))
        AND (:country IS NULL OR country = :country)
        AND (:category IS NULL OR category = :category)
        """)
    Page<Event> findEventsWithFiltersIlike(
        @Param("q") String query,
        @Param("date") LocalDateTime date,
        @Param("country") Event.Country country,
        @Param("category") Event.Category category,
        @Param("sort") String sort,
        Pageable pageable
    );

    List<Event> findByCountryOrderByRatioDescDateDesc(Event.Country country);

    List<Event> findByCategoryOrderByRatioDescDateDesc(Event.Category category);

    List<Event> findByCountryAndCategoryOrderByRatioDescDateDesc(Event.Country country, Event.Category category);

    @Query("SELECT e FROM Event e WHERE e.date >= :startDate AND e.date <= :endDate ORDER BY e.ratio DESC, e.date DESC")
    List<Event> findEventsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Required methods for EventService
    @Query("SELECT e FROM Event e WHERE e.date >= :startDate AND e.date <= :endDate ORDER BY e.ratio DESC, e.date DESC")
    Page<Event> findByDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE DATE(e.date) = DATE(:date) ORDER BY e.ratio DESC, e.date DESC")
    Page<Event> findByDate(@Param("date") LocalDateTime date, Pageable pageable);
}
