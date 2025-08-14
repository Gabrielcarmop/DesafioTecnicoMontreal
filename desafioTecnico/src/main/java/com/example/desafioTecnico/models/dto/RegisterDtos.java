package com.example.desafioTecnico.models.dto;

import java.util.Set;

public class RegisterDtos {
    public record RegisterRequest(String username, String password, String confirmPassword, Set<String> roles) { }
    public record RegisterResponse(Long id, String username, Set<String> roles) { }
}
