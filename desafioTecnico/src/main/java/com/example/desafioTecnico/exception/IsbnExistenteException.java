package com.example.desafioTecnico.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class IsbnExistenteException extends RuntimeException {
    public IsbnExistenteException(String message) {
        super(message);
    }
}