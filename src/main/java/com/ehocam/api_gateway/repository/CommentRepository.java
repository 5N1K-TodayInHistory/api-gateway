package com.ehocam.api_gateway.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ehocam.api_gateway.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByEventIdOrderByCreatedAtDesc(Long eventId, Pageable pageable);

    List<Comment> findByEventIdOrderByCreatedAtDesc(Long eventId);

    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.eventId = :eventId")
    long countByEventId(@Param("eventId") Long eventId);

    @Query("SELECT c FROM Comment c WHERE c.eventId = :eventId AND c.userId = :userId ORDER BY c.createdAt DESC")
    List<Comment> findByEventIdAndUserId(@Param("eventId") Long eventId, @Param("userId") Long userId);
}
