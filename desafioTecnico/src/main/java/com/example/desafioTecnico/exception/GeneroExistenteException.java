package com.example.desafioTecnico.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class GeneroExistenteException extends RuntimeException {
    public GeneroExistenteException(String message) {
        super(message);
    }
}