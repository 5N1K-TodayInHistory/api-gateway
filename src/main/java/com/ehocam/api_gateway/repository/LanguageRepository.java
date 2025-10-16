package com.ehocam.api_gateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ehocam.api_gateway.entity.Language;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {

    /**
     * Find all languages ordered by code
     */
    List<Language> findAllByOrderByCodeAsc();
}
