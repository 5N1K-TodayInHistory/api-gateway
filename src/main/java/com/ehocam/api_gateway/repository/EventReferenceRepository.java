package com.ehocam.api_gateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ehocam.api_gateway.entity.EventReference;

@Repository
public interface EventReferenceRepository extends JpaRepository<EventReference, Long> {

    /**
     * Find all references for an event
     */
    List<EventReference> findByEventIdOrderByCreatedAtAsc(Long eventId);
}
