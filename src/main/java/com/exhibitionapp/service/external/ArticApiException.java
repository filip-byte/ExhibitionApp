package com.exhibitionapp.service.external;

import org.springframework.http.HttpStatus;

public class ArticApiException extends RuntimeException {

    private final HttpStatus status;

    public ArticApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}