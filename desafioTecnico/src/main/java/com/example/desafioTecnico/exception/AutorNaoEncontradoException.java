package com.example.desafioTecnico.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AutorNaoEncontradoException extends RuntimeException {
    
    public AutorNaoEncontradoException(String message) {
        super(message);
    }
    
    public AutorNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}