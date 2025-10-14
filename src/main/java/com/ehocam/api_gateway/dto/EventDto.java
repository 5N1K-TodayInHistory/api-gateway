package com.ehocam.api_gateway.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.ehocam.api_gateway.entity.Event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EventDto {

    public static class Create {
        @NotBlank
        private String title;
        
        @NotBlank
        private String summary;
        
        @NotBlank
        private String content;
        
        @NotNull
        private LocalDateTime date;
        
        @NotNull
        private Event.Category category;
        
        @NotNull
        private Event.Country country;
        
        private MediaDto media;
        
        private Map<String, I18nContentDto> i18n;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public LocalDateTime getDate() { return date; }
        public void setDate(LocalDateTime date) { this.date = date; }
        public Event.Category getCategory() { return category; }
        public void setCategory(Event.Category category) { this.category = category; }
        public Event.Country getCountry() { return country; }
        public void setCountry(Event.Country country) { this.country = country; }
        public MediaDto getMedia() { return media; }
        public void setMedia(MediaDto media) { this.media = media; }
        public Map<String, I18nContentDto> getI18n() { return i18n; }
        public void setI18n(Map<String, I18nContentDto> i18n) { this.i18n = i18n; }
    }

    public static class Update {
        private String title;
        private String summary;
        private String content;
        private LocalDateTime date;
        private Event.Category category;
        private Event.Country country;
        private MediaDto media;
        private Map<String, I18nContentDto> i18n;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public LocalDateTime getDate() { return date; }
        public void setDate(LocalDateTime date) { this.date = date; }
        public Event.Category getCategory() { return category; }
        public void setCategory(Event.Category category) { this.category = category; }
        public Event.Country getCountry() { return country; }
        public void setCountry(Event.Country country) { this.country = country; }
        public MediaDto getMedia() { return media; }
        public void setMedia(MediaDto media) { this.media = media; }
        public Map<String, I18nContentDto> getI18n() { return i18n; }
        public void setI18n(Map<String, I18nContentDto> i18n) { this.i18n = i18n; }
    }

    public static class Response {
        private Long id;
        private String title;
        private String summary;
        private String content;
        private LocalDateTime date;
        private Event.Category category;
        private Event.Country country;
        private MediaDto media;
        private EngagementDto engagement;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Map<String, I18nContentDto> i18n;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public LocalDateTime getDate() { return date; }
        public void setDate(LocalDateTime date) { this.date = date; }
        public Event.Category getCategory() { return category; }
        public void setCategory(Event.Category category) { this.category = category; }
        public Event.Country getCountry() { return country; }
        public void setCountry(Event.Country country) { this.country = country; }
        public MediaDto getMedia() { return media; }
        public void setMedia(MediaDto media) { this.media = media; }
        public EngagementDto getEngagement() { return engagement; }
        public void setEngagement(EngagementDto engagement) { this.engagement = engagement; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        public Map<String, I18nContentDto> getI18n() { return i18n; }
        public void setI18n(Map<String, I18nContentDto> i18n) { this.i18n = i18n; }
    }

    public static class MediaDto {
        private String thumbnailUrl;
        private String bannerUrl;
        private String youtubeId;
        private String audioUrl;

        // Getters and Setters
        public String getThumbnailUrl() { return thumbnailUrl; }
        public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
        public String getBannerUrl() { return bannerUrl; }
        public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
        public String getYoutubeId() { return youtubeId; }
        public void setYoutubeId(String youtubeId) { this.youtubeId = youtubeId; }
        public String getAudioUrl() { return audioUrl; }
        public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    }

    public static class EngagementDto {
        private long likes = 0;
        private long comments = 0;
        private long shares = 0;

        // Getters and Setters
        public long getLikes() { return likes; }
        public void setLikes(long likes) { this.likes = likes; }
        public long getComments() { return comments; }
        public void setComments(long comments) { this.comments = comments; }
        public long getShares() { return shares; }
        public void setShares(long shares) { this.shares = shares; }
    }

    public static class I18nContentDto {
        private String title;
        private String summary;
        private String content;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
