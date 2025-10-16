package com.ehocam.api_gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CountryDto {

    public static class Response {
        @JsonProperty("code")
        private String code;

        @JsonProperty("name")
        private String name; // Single name in requested language

        @JsonProperty("flag")
        private String flag;

        // Constructors
        public Response() {}

        public Response(String code, String name, String flag) {
            this.code = code;
            this.name = name;
            this.flag = flag;
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

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        @Override
        public String toString() {
            return "CountryDto.Response{" +
                    "code='" + code + '\'' +
                    ", name='" + name + '\'' +
                    ", flag='" + flag + '\'' +
                    '}';
        }
    }
}