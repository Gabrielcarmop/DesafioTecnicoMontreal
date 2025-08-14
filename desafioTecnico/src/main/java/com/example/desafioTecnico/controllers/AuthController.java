package com.example.desafioTecnico.controllers;

import com.example.desafioTecnico.models.dto.RegisterDtos;
import com.example.desafioTecnico.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    public record LoginRequest(String username, String password) {}
    public record LoginResponse(String token, Set<String> roles) {}

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest body) {
        String token = authService.authenticateAndGenerateToken(body.username(), body.password());
        Set<String> roles = authService.getUserRoles(body.username());
        return new LoginResponse(token, roles);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterDtos.RegisterResponse register(@RequestBody RegisterDtos.RegisterRequest body) {
        var user = authService.register(body);
        return new RegisterDtos.RegisterResponse(user.getId(), user.getUsername(),
                user.getRolesAsString());
    }
}