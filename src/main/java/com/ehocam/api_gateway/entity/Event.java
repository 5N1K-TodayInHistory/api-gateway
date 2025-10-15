package com.ehocam.api_gateway.entity;

import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_events_date_country_category", columnList = "date DESC, country, category"),
    @Index(name = "idx_events_created_at", columnList = "createdAt DESC")
})
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private Country country;

    @Column(name = "ratio", nullable = false)
    private Integer ratio = 50; // Default importance value (1-100)

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "media", columnDefinition = "jsonb")
    private Media media;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "engagement", columnDefinition = "jsonb")
    private Engagement engagement;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "i18n", columnDefinition = "jsonb")
    private Map<String, I18nContent> i18n;

    // Constructors
    public Event() {}

    public Event(String title, String summary, String content, LocalDateTime date, Category category, Country country) {
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.date = date;
        this.category = category;
        this.country = country;
        this.engagement = new Engagement();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Integer getRatio() {
        return ratio;
    }

    public void setRatio(Integer ratio) {
        this.ratio = ratio;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public Engagement getEngagement() {
        return engagement;
    }

    public void setEngagement(Engagement engagement) {
        this.engagement = engagement;
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

    public Map<String, I18nContent> getI18n() {
        return i18n;
    }

    public void setI18n(Map<String, I18nContent> i18n) {
        this.i18n = i18n;
    }

    // Enums
    public enum Category {
        SCIENCE, POLITICS, SPORTS, HISTORY, ENTERTAINMENT, TECHNOLOGY, HEALTH, BUSINESS, WORLD, LOCAL
    }

    public enum Country {
        TR, US, GB, DE, FR, ES, IT, RU, CN, JP, BR, IN, AU, CA, MX, AR, CL, CO, PE, VE, EG, ZA, NG, KE, MA, TN, DZ, LY, SD, ET, GH, CI, SN, ML, BF, NE, TD, CM, CF, CG, CD, AO, ZM, ZW, BW, NA, SZ, LS, MG, MU, SC, KM, DJ, SO, ER, SS, ALL
    }

    // Nested classes for JSONB
    public static class Media {
        private String thumbnailUrl;
        private String bannerUrl;
        private String youtubeId;
        private String audioUrl;
        private Map<String, MediaI18n> i18n; // Language-based media files

        public Media() {}

        // Getters and Setters
        public String getThumbnailUrl() { return thumbnailUrl; }
        public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
        public String getBannerUrl() { return bannerUrl; }
        public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
        public String getYoutubeId() { return youtubeId; }
        public void setYoutubeId(String youtubeId) { this.youtubeId = youtubeId; }
        public String getAudioUrl() { return audioUrl; }
        public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
        public Map<String, MediaI18n> getI18n() { return i18n; }
        public void setI18n(Map<String, MediaI18n> i18n) { this.i18n = i18n; }
    }

    public static class MediaI18n {
        private String audioUrl; // Language-based audio file
        private String thumbnailUrl; // Language-based thumbnail (optional)
        private String bannerUrl; // Language-based banner (optional)

        public MediaI18n() {}

        public MediaI18n(String audioUrl) {
            this.audioUrl = audioUrl;
        }

        // Getters and Setters
        public String getAudioUrl() { return audioUrl; }
        public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
        public String getThumbnailUrl() { return thumbnailUrl; }
        public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
        public String getBannerUrl() { return bannerUrl; }
        public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
    }

    public static class Engagement {
        private long likes = 0;
        private long comments = 0;
        private long shares = 0;

        public Engagement() {}

        // Getters and Setters
        public long getLikes() { return likes; }
        public void setLikes(long likes) { this.likes = likes; }
        public long getComments() { return comments; }
        public void setComments(long comments) { this.comments = comments; }
        public long getShares() { return shares; }
        public void setShares(long shares) { this.shares = shares; }
    }

    public static class I18nContent {
        private String title;
        private String summary;
        private String content;

        public I18nContent() {}

        public I18nContent(String title, String summary, String content) {
            this.title = title;
            this.summary = summary;
            this.content = content;
        }

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
