package com.ehocam.api_gateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ehocam.api_gateway.entity.EventType;

@Repository
public interface EventTypeRepository extends JpaRepository<EventType, Long> {

    /**
     * Find all event types ordered by code
     */
    List<EventType> findAllByOrderByCodeAsc();
}
