package com.ehocam.api_gateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ehocam.api_gateway.entity.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    /**
     * Find all countries ordered by code
     */
    List<Country> findAllByOrderByCodeAsc();

    /**
     * Find all active countries ordered by code
     */
    List<Country> findAllByIsActiveTrueOrderByCodeAsc();
}