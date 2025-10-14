package com.ehocam.api_gateway.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ehocam.api_gateway.entity.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByEventIdAndUserId(Long eventId, Long userId);

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.eventId = :eventId")
    long countByEventId(@Param("eventId") Long eventId);

    @Query("SELECT l FROM Like l WHERE l.eventId = :eventId ORDER BY l.createdAt DESC")
    List<Like> findByEventIdOrderByCreatedAtDesc(@Param("eventId") Long eventId);

    @Query("SELECT l FROM Like l WHERE l.userId = :userId ORDER BY l.createdAt DESC")
    List<Like> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    void deleteByEventIdAndUserId(Long eventId, Long userId);
}
