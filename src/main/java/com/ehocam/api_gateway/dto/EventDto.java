package com.ehocam.api_gateway.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventDto {

    public static class Response {
        @JsonProperty("id")
        private String id;

        @JsonProperty("title")
        private String title;

        @JsonProperty("description")
        private String description;

        @JsonProperty("content")
        private String content;

        @JsonProperty("date")
        private String date;

        @JsonProperty("type")
        private String type;

        @JsonProperty("country")
        private String country;

        @JsonProperty("imageUrl")
        private String imageUrl;

        @JsonProperty("videoUrls")
        private List<String> videoUrls;

        @JsonProperty("audioUrls")
        private List<String> audioUrls;

        @JsonProperty("references")
        private List<ReferenceDto> references;

        @JsonProperty("likes")
        private Integer likes;

        @JsonProperty("comments")
        private Integer comments;

        // Constructors
        public Response() {}

        public Response(String id, String title, String description, String content, String date,
                       String type, String country, String imageUrl, List<String> videoUrls,
                       List<String> audioUrls, List<ReferenceDto> references, Integer likes,
                       Integer comments) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.content = content;
            this.date = date;
            this.type = type;
            this.country = country;
            this.imageUrl = imageUrl;
            this.videoUrls = videoUrls;
            this.audioUrls = audioUrls;
            this.references = references;
            this.likes = likes;
            this.comments = comments;
        }

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
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

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
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

        public List<ReferenceDto> getReferences() {
            return references;
        }

        public void setReferences(List<ReferenceDto> references) {
            this.references = references;
        }

        public Integer getLikes() {
            return likes;
        }

        public void setLikes(Integer likes) {
            this.likes = likes;
        }

        public Integer getComments() {
            return comments;
        }

        public void setComments(Integer comments) {
            this.comments = comments;
        }

        @Override
        public String toString() {
            return "EventDto.Response{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", content='" + content + '\'' +
                    ", date='" + date + '\'' +
                    ", type='" + type + '\'' +
                    ", country='" + country + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", videoUrls=" + videoUrls +
                    ", audioUrls=" + audioUrls +
                    ", references=" + references +
                    ", likes=" + likes +
                    ", comments=" + comments +
                    '}';
        }
    }

    public static class ReferenceDto {
        @JsonProperty("title")
        private String title;

        @JsonProperty("url")
        private String url;

        // Constructors
        public ReferenceDto() {}

        public ReferenceDto(String title, String url) {
            this.title = title;
            this.url = url;
        }

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return "ReferenceDto{" +
                    "title='" + title + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    public static class LikeRequest {
        @JsonProperty("eventId")
        private Long eventId;

        // Constructors
        public LikeRequest() {}

        public LikeRequest(Long eventId) {
            this.eventId = eventId;
        }

        // Getters and Setters
        public Long getEventId() {
            return eventId;
        }

        public void setEventId(Long eventId) {
            this.eventId = eventId;
        }
    }

    public static class LikeResponse {
        @JsonProperty("success")
        private Boolean success;

        @JsonProperty("liked")
        private Boolean liked;

        @JsonProperty("likesCount")
        private Integer likesCount;

        // Constructors
        public LikeResponse() {}

        public LikeResponse(Boolean success, Boolean liked, Integer likesCount) {
            this.success = success;
            this.liked = liked;
            this.likesCount = likesCount;
        }

        // Getters and Setters
        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public Boolean getLiked() {
            return liked;
        }

        public void setLiked(Boolean liked) {
            this.liked = liked;
        }

        public Integer getLikesCount() {
            return likesCount;
        }

        public void setLikesCount(Integer likesCount) {
            this.likesCount = likesCount;
        }
    }
}