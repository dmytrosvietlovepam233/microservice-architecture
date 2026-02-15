package com.learn.microservice.songservice.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ErrorResponse {
    private String errorMessage;
    private String errorCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> details;

    public ErrorResponse(String errorMessage, String errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public ErrorResponse(String errorMessage, String errorCode, Map<String, String> details) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.details = details;
    }
}