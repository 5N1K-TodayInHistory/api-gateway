package com.ehocam.api_gateway.entity;

import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "countries", indexes = {
    @Index(name = "idx_countries_code", columnList = "code")
})
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 3)
    @Column(nullable = false, unique = true, length = 3)
    private String code;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, String> name; // Multilingual names: {"en": "Turkey", "tr": "Türkiye"}

    @NotBlank
    @Size(max = 500)
    @Column(name = "flag_url", nullable = false, length = 500)
    private String flag; // Flag emoji or URL

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Country() {}

    public Country(String code, Map<String, String> name, String flag) {
        this.code = code;
        this.name = name;
        this.flag = flag;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
    public String getNameForLanguage(String languageCode) {
        if (name == null) {
            return null;
        }
        return name.get(languageCode);
    }

    public String getDefaultName() {
        if (name == null) {
            return null;
        }
        // Fallback to English if available
        return name.get("en");
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name=" + name +
                ", flag='" + flag + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
