package com.ehocam.api_gateway.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_events_date", columnList = "date DESC"),
    @Index(name = "idx_events_type", columnList = "type"),
    @Index(name = "idx_events_country", columnList = "country"),
    @Index(name = "idx_events_date_type_country", columnList = "date DESC, type, country"),
    @Index(name = "idx_events_likes_count", columnList = "likes_count DESC"),
    @Index(name = "idx_events_created_at", columnList = "created_at DESC")
})
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, String> title; // Multilingual titles: {"en": "Title", "tr": "Başlık"}

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, String> description; // Multilingual descriptions

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, String> content; // Multilingual content

    @NotNull
    @Column(nullable = false)
    private LocalDateTime date;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String type; // Event type code (politics, science, sports, etc.)

    @NotBlank
    @Size(max = 3)
    @Column(nullable = false, length = 3)
    private String country; // Country code (TR, US, ALL, etc.)

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "images", columnDefinition = "jsonb")
    private List<EventImage> images; // Array of images with different sizes

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "video_urls", columnDefinition = "jsonb")
    private List<String> videoUrls; // Array of video URLs

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "audio_urls", columnDefinition = "jsonb")
    private List<String> audioUrls; // Array of audio URLs

    @Column(name = "likes_count", nullable = false)
    private Integer likesCount = 0;

    @Column(name = "comments_count", nullable = false)
    private Integer commentsCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Performance optimization fields
    @Column(name = "month_day", insertable = false, updatable = false)
    private String monthDay; // Generated column: MM-DD format

    @Column(name = "score")
    private Short score; // Importance score 1-100

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "importance_reason", columnDefinition = "jsonb")
    private Map<String, String> importanceReason; // Multilingual importance explanation

    // Relationships
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventLike> likes;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventReference> references;

    // Constructors
    public Event() {}

    public Event(Map<String, String> title, Map<String, String> description, Map<String, String> content,
                 LocalDateTime date, String type, String country) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.date = date;
        this.type = type;
        this.country = country;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    public Map<String, String> getContent() {
        return content;
    }

    public void setContent(Map<String, String> content) {
        this.content = content;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<EventImage> getImages() {
        return images;
    }

    public void setImages(List<EventImage> images) {
        this.images = images;
    }

    public List<String> getVideoUrls() {
        return videoUrls;
    }

    public void setVideoUrls(List<String> videoUrls) {
        this.videoUrls = videoUrls;
    }

    public List<String> getAudioUrls() {
        return audioUrls;
    }

    public void setAudioUrls(List<String> audioUrls) {
        this.audioUrls = audioUrls;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
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

    public List<EventLike> getLikes() {
        return likes;
    }

    public void setLikes(List<EventLike> likes) {
        this.likes = likes;
    }

    public List<EventReference> getReferences() {
        return references;
    }

    public void setReferences(List<EventReference> references) {
        this.references = references;
    }

    // Getters and Setters for new performance fields
    public String getMonthDay() {
        return monthDay;
    }

    public void setMonthDay(String monthDay) {
        this.monthDay = monthDay;
    }

    public Short getScore() {
        return score;
    }

    public void setScore(Short score) {
        this.score = score;
    }

    public Map<String, String> getImportanceReason() {
        return importanceReason;
    }

    public void setImportanceReason(Map<String, String> importanceReason) {
        this.importanceReason = importanceReason;
    }

    // Helper methods for multilingual content
    public String getTitleForLanguage(String languageCode) {
        if (title == null) {
            return null;
        }
        return title.get(languageCode);
    }

    public String getDescriptionForLanguage(String languageCode) {
        if (description == null) {
            return null;
        }
        return description.get(languageCode);
    }

    public String getContentForLanguage(String languageCode) {
        if (content == null) {
            return null;
        }
        return content.get(languageCode);
    }

    public String getDefaultTitle() {
        if (title == null) {
            return null;
        }
        return title.get("en");
    }

    public String getDefaultDescription() {
        if (description == null) {
            return null;
        }
        return description.get("en");
    }

    public String getDefaultContent() {
        if (content == null) {
            return null;
        }
        return content.get("en");
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title=" + title +
                ", description=" + description +
                ", content=" + content +
                ", date=" + date +
                ", type='" + type + '\'' +
                ", country='" + country + '\'' +
                ", images=" + images +
                ", videoUrls=" + videoUrls +
                ", audioUrls=" + audioUrls +
                ", likesCount=" + likesCount +
                ", commentsCount=" + commentsCount +
                ", monthDay='" + monthDay + '\'' +
                ", score=" + score +
                ", importanceReason=" + importanceReason +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    /**
     * EventImage inner class for storing image information
     * Used for JSONB storage in PostgreSQL
     */
    public static class EventImage {
        private String type; // "medium", "large", "large2x"
        private String image_url;
        private Boolean is_default;

        // Default constructor for Jackson
        public EventImage() {}

        public EventImage(String type, String image_url, Boolean is_default) {
            this.type = type;
            this.image_url = image_url;
            this.is_default = is_default;
        }

        // Getters and Setters
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public Boolean getIs_default() {
            return is_default;
        }

        public void setIs_default(Boolean is_default) {
            this.is_default = is_default;
        }

        @Override
        public String toString() {
            return "EventImage{" +
                    "type='" + type + '\'' +
                    ", image_url='" + image_url + '\'' +
                    ", is_default=" + is_default +
                    '}';
        }
    }
}