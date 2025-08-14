package com.example.desafioTecnico.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GeneroNaoEncontradoException extends RuntimeException {
    public GeneroNaoEncontradoException(String message) {
        super(message);
    }
}