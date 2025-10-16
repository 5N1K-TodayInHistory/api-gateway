package com.ehocam.api_gateway.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ehocam.api_gateway.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Find events with filters and pagination
     */
    @Query(value = """
        SELECT * FROM events 
        WHERE (:date IS NULL OR DATE(date) = DATE(:date))
        AND (:type IS NULL OR type = :type)
        AND (:country IS NULL OR country = :country)
        ORDER BY 
            CASE WHEN :sort = 'DATE_DESC' THEN date END DESC,
            CASE WHEN :sort = 'LIKES_DESC' THEN likes_count END DESC,
            CASE WHEN :sort = 'RECENT' THEN created_at END DESC,
            date DESC
        """, nativeQuery = true, countQuery = """
        SELECT COUNT(*) FROM events 
        WHERE (:date IS NULL OR DATE(date) = DATE(:date))
        AND (:type IS NULL OR type = :type)
        AND (:country IS NULL OR country = :country)
        """)
    Page<Event> findEventsWithFilters(
        @Param("date") LocalDateTime date,
        @Param("type") String type,
        @Param("country") String country,
        @Param("sort") String sort,
        Pageable pageable
    );

    /**
     * Find events by date range
     */
    @Query("SELECT e FROM Event e WHERE e.date >= :startDate AND e.date <= :endDate ORDER BY e.date DESC")
    Page<Event> findByDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    /**
     * Find events by specific date
     */
    @Query("SELECT e FROM Event e WHERE e.date >= :startOfDay AND e.date < :endOfDay ORDER BY e.date DESC")
    Page<Event> findByDate(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay, Pageable pageable);

    /**
     * Find events by type
     */
    Page<Event> findByTypeOrderByDateDesc(String type, Pageable pageable);

    /**
     * Find events by country
     */
    Page<Event> findByCountryOrderByDateDesc(String country, Pageable pageable);

    /**
     * Find events by type and country
     */
    Page<Event> findByTypeAndCountryOrderByDateDesc(String type, String country, Pageable pageable);

    /**
     * Find today's events
     */
    @Query("SELECT e FROM Event e WHERE e.date >= :startOfToday AND e.date < :endOfToday ORDER BY e.date DESC")
    Page<Event> findTodaysEvents(@Param("startOfToday") LocalDateTime startOfToday, @Param("endOfToday") LocalDateTime endOfToday, Pageable pageable);

    /**
     * Find today's events by type
     */
    @Query("SELECT e FROM Event e WHERE e.date >= :startOfToday AND e.date < :endOfToday AND e.type = :type ORDER BY e.date DESC")
    Page<Event> findTodaysEventsByType(@Param("startOfToday") LocalDateTime startOfToday, @Param("endOfToday") LocalDateTime endOfToday, @Param("type") String type, Pageable pageable);

    /**
     * Find today's events by country
     */
    @Query("SELECT e FROM Event e WHERE e.date >= :startOfToday AND e.date < :endOfToday AND e.country = :country ORDER BY e.date DESC")
    Page<Event> findTodaysEventsByCountry(@Param("startOfToday") LocalDateTime startOfToday, @Param("endOfToday") LocalDateTime endOfToday, @Param("country") String country, Pageable pageable);

    /**
     * Find today's events by type and country
     */
    @Query("SELECT e FROM Event e WHERE e.date >= :startOfToday AND e.date < :endOfToday AND e.type = :type AND e.country = :country ORDER BY e.date DESC")
    Page<Event> findTodaysEventsByTypeAndCountry(@Param("startOfToday") LocalDateTime startOfToday, @Param("endOfToday") LocalDateTime endOfToday, @Param("type") String type, @Param("country") String country, Pageable pageable);
}