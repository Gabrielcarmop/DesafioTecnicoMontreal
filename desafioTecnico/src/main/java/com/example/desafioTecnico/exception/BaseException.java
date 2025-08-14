package com.example.desafioTecnico.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {
    
    private final HttpStatus status;
    
    protected BaseException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
    
    protected BaseException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
    
    public HttpStatus getStatus() {
        return status;
    }
}