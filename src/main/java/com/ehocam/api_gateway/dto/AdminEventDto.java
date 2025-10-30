package com.ehocam.api_gateway.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.ehocam.api_gateway.entity.Event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AdminEventDto {

    @Schema(description = "Event creation request")
    public static class Create {
        @NotNull(message = "Title is required")
        @Schema(description = "Multilingual titles", example = "{\"en\": \"Event Title\", \"tr\": \"Etkinlik Başlığı\"}")
        private Map<String, String> title;

        @NotNull(message = "Description is required")
        @Schema(description = "Multilingual descriptions", example = "{\"en\": \"Event Description\", \"tr\": \"Etkinlik Açıklaması\"}")
        private Map<String, String> description;

        @NotNull(message = "Content is required")
        @Schema(description = "Multilingual content", example = "{\"en\": \"Event Content\", \"tr\": \"Etkinlik İçeriği\"}")
        private Map<String, String> content;

        @NotNull(message = "Date is required")
        @Schema(description = "Event date and time")
        private LocalDateTime date;

        @NotBlank(message = "Type is required")
        @Size(max = 20, message = "Type must not exceed 20 characters")
        @Schema(description = "Event type code", example = "politics")
        private String type;

        @NotBlank(message = "Country is required")
        @Size(max = 3, message = "Country code must not exceed 3 characters")
        @Schema(description = "Country code", example = "TR")
        private String country;

        @Size(max = 500, message = "Image URL must not exceed 500 characters")
        @Schema(description = "Main image URL")
        private String imageUrl;

        @Schema(description = "Array of video URLs")
        private List<String> videoUrls;

        @Schema(description = "Array of audio URLs")
        private List<String> audioUrls;

        @Schema(description = "Importance score (1-100)")
        private Short score;

        @Schema(description = "Multilingual importance reason", example = "{\"en\": \"Important for economy\", \"tr\": \"Ekonomi için önemli\"}")
        private Map<String, String> importanceReason;

        // Getters and Setters
        public Map<String, String> getTitle() { return title; }
        public void setTitle(Map<String, String> title) { this.title = title; }
        public Map<String, String> getDescription() { return description; }
        public void setDescription(Map<String, String> description) { this.description = description; }
        public Map<String, String> getContent() { return content; }
        public void setContent(Map<String, String> content) { this.content = content; }
        public LocalDateTime getDate() { return date; }
        public void setDate(LocalDateTime date) { this.date = date; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public List<String> getVideoUrls() { return videoUrls; }
        public void setVideoUrls(List<String> videoUrls) { this.videoUrls = videoUrls; }
        public List<String> getAudioUrls() { return audioUrls; }
        public void setAudioUrls(List<String> audioUrls) { this.audioUrls = audioUrls; }
        public Short getScore() { return score; }
        public void setScore(Short score) { this.score = score; }
        public Map<String, String> getImportanceReason() { return importanceReason; }
        public void setImportanceReason(Map<String, String> importanceReason) { this.importanceReason = importanceReason; }
    }

    @Schema(description = "Event update request")
    public static class Update {
        @Schema(description = "Multilingual titles", example = "{\"en\": \"Updated Event Title\", \"tr\": \"Güncellenmiş Etkinlik Başlığı\"}")
        private Map<String, String> title;

        @Schema(description = "Multilingual descriptions", example = "{\"en\": \"Updated Event Description\", \"tr\": \"Güncellenmiş Etkinlik Açıklaması\"}")
        private Map<String, String> description;

        @Schema(description = "Multilingual content", example = "{\"en\": \"Updated Event Content\", \"tr\": \"Güncellenmiş Etkinlik İçeriği\"}")
        private Map<String, String> content;

        @Schema(description = "Event date and time")
        private LocalDateTime date;

        @Size(max = 20, message = "Type must not exceed 20 characters")
        @Schema(description = "Event type code", example = "politics")
        private String type;

        @Size(max = 3, message = "Country code must not exceed 3 characters")
        @Schema(description = "Country code", example = "TR")
        private String country;

        @Size(max = 500, message = "Image URL must not exceed 500 characters")
        @Schema(description = "Main image URL")
        private String imageUrl;

        @Schema(description = "Array of video URLs")
        private List<String> videoUrls;

        @Schema(description = "Array of audio URLs")
        private List<String> audioUrls;

        @Schema(description = "Importance score (1-100)")
        private Short score;

        @Schema(description = "Multilingual importance reason", example = "{\"en\": \"Important for economy\", \"tr\": \"Ekonomi için önemli\"}")
        private Map<String, String> importanceReason;

        // Getters and Setters
        public Map<String, String> getTitle() { return title; }
        public void setTitle(Map<String, String> title) { this.title = title; }
        public Map<String, String> getDescription() { return description; }
        public void setDescription(Map<String, String> description) { this.description = description; }
        public Map<String, String> getContent() { return content; }
        public void setContent(Map<String, String> content) { this.content = content; }
        public LocalDateTime getDate() { return date; }
        public void setDate(LocalDateTime date) { this.date = date; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public List<String> getVideoUrls() { return videoUrls; }
        public void setVideoUrls(List<String> videoUrls) { this.videoUrls = videoUrls; }
        public List<String> getAudioUrls() { return audioUrls; }
        public void setAudioUrls(List<String> audioUrls) { this.audioUrls = audioUrls; }
        public Short getScore() { return score; }
        public void setScore(Short score) { this.score = score; }
        public Map<String, String> getImportanceReason() { return importanceReason; }
        public void setImportanceReason(Map<String, String> importanceReason) { this.importanceReason = importanceReason; }
    }

    @Schema(description = "Admin event response with full details")
    public static class Response {
        @Schema(description = "Event ID")
        private Long id;

        @Schema(description = "Multilingual titles")
        private Map<String, String> title;

        @Schema(description = "Multilingual descriptions")
        private Map<String, String> description;

        @Schema(description = "Multilingual content")
        private Map<String, String> content;

        @Schema(description = "Event date and time")
        private LocalDateTime date;

        @Schema(description = "Event type code")
        private String type;

        @Schema(description = "Country code")
        private String country;

        @Schema(description = "Main image URL")
        private String imageUrl;

        @Schema(description = "Array of video URLs")
        private List<String> videoUrls;

        @Schema(description = "Array of audio URLs")
        private List<String> audioUrls;

        @Schema(description = "Number of likes")
        private Integer likesCount;

        @Schema(description = "Number of comments")
        private Integer commentsCount;

        @Schema(description = "Importance score")
        private Short score;

        @Schema(description = "Multilingual importance reason")
        private Map<String, String> importanceReason;

        @Schema(description = "Creation timestamp")
        private LocalDateTime createdAt;

        @Schema(description = "Last update timestamp")
        private LocalDateTime updatedAt;

        // Constructors
        public Response() {}

        public Response(Event event) {
            this.id = event.getId();
            this.title = event.getTitle();
            this.description = event.getDescription();
            this.content = event.getContent();
            this.date = event.getDate();
            this.type = event.getType();
            this.country = event.getCountry();
            this.imageUrl = event.getImageUrl();
            this.videoUrls = event.getVideoUrls();
            this.audioUrls = event.getAudioUrls();
            this.likesCount = event.getLikesCount();
            this.commentsCount = event.getCommentsCount();
            this.score = event.getScore();
            this.importanceReason = event.getImportanceReason();
            this.createdAt = event.getCreatedAt();
            this.updatedAt = event.getUpdatedAt();
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Map<String, String> getTitle() { return title; }
        public void setTitle(Map<String, String> title) { this.title = title; }
        public Map<String, String> getDescription() { return description; }
        public void setDescription(Map<String, String> description) { this.description = description; }
        public Map<String, String> getContent() { return content; }
        public void setContent(Map<String, String> content) { this.content = content; }
        public LocalDateTime getDate() { return date; }
        public void setDate(LocalDateTime date) { this.date = date; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public List<String> getVideoUrls() { return videoUrls; }
        public void setVideoUrls(List<String> videoUrls) { this.videoUrls = videoUrls; }
        public List<String> getAudioUrls() { return audioUrls; }
        public void setAudioUrls(List<String> audioUrls) { this.audioUrls = audioUrls; }
        public Integer getLikesCount() { return likesCount; }
        public void setLikesCount(Integer likesCount) { this.likesCount = likesCount; }
        public Integer getCommentsCount() { return commentsCount; }
        public void setCommentsCount(Integer commentsCount) { this.commentsCount = commentsCount; }
        public Short getScore() { return score; }
        public void setScore(Short score) { this.score = score; }
        public Map<String, String> getImportanceReason() { return importanceReason; }
        public void setImportanceReason(Map<String, String> importanceReason) { this.importanceReason = importanceReason; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}

