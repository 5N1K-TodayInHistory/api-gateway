package com.ehocam.api_gateway.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ehocam.api_gateway.entity.EventLike;

@Repository
public interface EventLikeRepository extends JpaRepository<EventLike, Long> {

    /**
     * Check if user has liked an event
     */
    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    /**
     * Find like by event and user
     */
    Optional<EventLike> findByEventIdAndUserId(Long eventId, Long userId);

    /**
     * Count likes for an event
     */
    long countByEventId(Long eventId);

    /**
     * Delete like by event and user
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM EventLike el WHERE el.event.id = :eventId AND el.userId = :userId")
    void deleteByEventIdAndUserId(@Param("eventId") Long eventId, @Param("userId") Long userId);
}
