package com.example.desafioTecnico.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AutorExistenteException extends RuntimeException {
    
    public AutorExistenteException(String message) {
        super(message);
    }
    
    public AutorExistenteException(String message, Throwable cause) {
        super(message, cause);
    }
}