package com.example.desafioTecnico.services;

import com.example.desafioTecnico.models.dto.RegisterDtos;
import com.example.desafioTecnico.models.entities.Usuario;
import com.example.desafioTecnico.models.enums.Role;
import com.example.desafioTecnico.repositories.UsuarioRepository;
import com.example.desafioTecnico.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public String authenticateAndGenerateToken(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Usuário ou senha inválidos"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new BadCredentialsException("Usuário ou senha inválidos");
        }

        Set<String> roles = usuario.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet());

        return jwtService.generateToken(usuario.getUsername(), roles);
    }

    public Set<String> getUserRoles(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"))
                .getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet());
    }

    public Usuario register(RegisterDtos.RegisterRequest req) {
        if (req.username() == null || req.username().isBlank()
                || req.password() == null || req.password().isBlank()
                || req.confirmPassword() == null) {
            throw new IllegalArgumentException("Campos inválidos");
        }
        if (!req.password().equals(req.confirmPassword())) {
            throw new IllegalArgumentException("Senhas não conferem");
        }
        if (req.roles() == null || req.roles().isEmpty()) {
            throw new IllegalArgumentException("Selecione pelo menos uma permissão");
        }

        if (usuarioRepository.findByUsername(req.username()).isPresent()) {
            throw new DataIntegrityViolationException("Usuário já existe");
        }

        var rolesEnum = req.roles().stream()
                .map(String::toUpperCase)
                .map(Role::valueOf)
                .collect(Collectors.toSet());

        var novo = new Usuario();
        novo.setUsername(req.username());
        novo.setPassword(passwordEncoder.encode(req.password()));
        novo.setRoles(rolesEnum);

        return usuarioRepository.save(novo);
    }
}
