package com.ehocam.api_gateway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private boolean success;
    private T data;
    private String error;
    
    // Constructors
    public ApiResponse() {}
    
    public ApiResponse(boolean success) {
        this.success = success;
    }
    
    public ApiResponse(boolean success, T data) {
        this.success = success;
        this.data = data;
    }
    
    public ApiResponse(boolean success, String error) {
        this.success = success;
        this.error = error;
    }
    
    public ApiResponse(boolean success, T data, String error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }
    
    // Static factory methods for common responses
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data);
    }
    
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true);
    }
    
    public static <T> ApiResponse<T> error(String error) {
        return new ApiResponse<>(false, error);
    }
    
    public static <T> ApiResponse<T> error(String error, T data) {
        return new ApiResponse<>(false, data, error);
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
}
