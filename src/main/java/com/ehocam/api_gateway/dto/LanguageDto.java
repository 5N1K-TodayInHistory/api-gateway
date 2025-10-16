package com.ehocam.api_gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LanguageDto {

    public static class Response {
        @JsonProperty("code")
        private String code;

        @JsonProperty("name")
        private String name; // Single name in requested language

        // Constructors
        public Response() {}

        public Response(String code, String name) {
            this.code = code;
            this.name = name;
        }

        // Getters and Setters
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "LanguageDto.Response{" +
                    "code='" + code + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
