package com.example.desafioTecnico.services;

import com.example.desafioTecnico.models.dto.RegisterDtos;
import com.example.desafioTecnico.models.entities.Usuario;
import com.example.desafioTecnico.models.enums.Role;
import com.example.desafioTecnico.repositories.UsuarioRepository;
import com.example.desafioTecnico.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService service;


    @Test
    @DisplayName("authenticateAndGenerateToken: deve gerar JWT quando credenciais corretas")
    void authenticate_success() {
        String username = "alice";
        String rawPass = "secret";
        String encodedPass = "{bcrypt}hash";
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword(encodedPass);
        u.setRoles(EnumSet.of(Role.ESCRITA, Role.LEITURA));

        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(u));
        when(passwordEncoder.matches(rawPass, encodedPass)).thenReturn(true);
        when(jwtService.generateToken(eq(username), anySet())).thenReturn("jwt-token");

        String token = service.authenticateAndGenerateToken(username, rawPass);

        assertEquals("jwt-token", token);
        ArgumentCaptor<Set<String>> rolesCap = ArgumentCaptor.forClass(Set.class);
        verify(jwtService).generateToken(eq(username), rolesCap.capture());
        assertTrue(rolesCap.getValue().containsAll(Set.of("ESCRITA", "LEITURA")));
    }

    @Test
    @DisplayName("authenticateAndGenerateToken: deve lançar BadCredentials quando usuário não encontrado")
    void authenticate_userNotFound() {
        when(usuarioRepository.findByUsername("bob")).thenReturn(Optional.empty());
        assertThrows(BadCredentialsException.class,
                () -> service.authenticateAndGenerateToken("bob", "x"));
    }

    @Test
    @DisplayName("authenticateAndGenerateToken: deve lançar BadCredentials quando senha inválida")
    void authenticate_wrongPassword() {
        String username = "carol";
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword("{bcrypt}hash");
        u.setRoles(EnumSet.of(Role.LEITURA));

        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("wrong", "{bcrypt}hash")).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> service.authenticateAndGenerateToken(username, "wrong"));
        verify(jwtService, never()).generateToken(anyString(), anySet());
    }


    @Test
    @DisplayName("getUserRoles: deve retornar nomes das roles quando usuário existe")
    void getUserRoles_success() {
        Usuario u = new Usuario();
        u.setUsername("dave");
        u.setRoles(EnumSet.of(Role.LEITURA, Role.ESCRITA));

        when(usuarioRepository.findByUsername("dave")).thenReturn(Optional.of(u));

        Set<String> roles = service.getUserRoles("dave");
        assertEquals(Set.of("ESCRITA", "LEITURA"), roles);
    }

    @Test
    @DisplayName("getUserRoles: deve lançar UsernameNotFound quando usuário não existe")
    void getUserRoles_notFound() {
        when(usuarioRepository.findByUsername("nope")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.getUserRoles("nope"));
    }

    @Nested
    @DisplayName("register: validações")
    class RegisterValidations {

        @Test
        void deveLancarQuandoUsernameNuloOuVazio() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.register(new RegisterDtos.RegisterRequest(null, "p", "p", Set.of("USER"))));

            assertThrows(IllegalArgumentException.class,
                    () -> service.register(new RegisterDtos.RegisterRequest("   ", "p", "p", Set.of("USER"))));
        }

        @Test
        void deveLancarQuandoPasswordNulaOuVaziaOuConfirmPasswordNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.register(new RegisterDtos.RegisterRequest("john", null, "x", Set.of("USER"))));

            assertThrows(IllegalArgumentException.class,
                    () -> service.register(new RegisterDtos.RegisterRequest("john", "   ", "   ", Set.of("USER"))));

            assertThrows(IllegalArgumentException.class,
                    () -> service.register(new RegisterDtos.RegisterRequest("john", "p", null, Set.of("USER"))));
        }

        @Test
        void deveLancarQuandoSenhasNaoConferem() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.register(new RegisterDtos.RegisterRequest("john", "p1", "p2", Set.of("USER"))));
        }

        @Test
        void deveLancarQuandoRolesVaziasOuNulas() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.register(new RegisterDtos.RegisterRequest("john", "p", "p", null)));

            assertThrows(IllegalArgumentException.class,
                    () -> service.register(new RegisterDtos.RegisterRequest("john", "p", "p", Set.of())));
        }

        @Test
        void deveLancarQuandoUsuarioJaExiste() {
            when(usuarioRepository.findByUsername("john")).thenReturn(Optional.of(new Usuario()));

            assertThrows(DataIntegrityViolationException.class,
                    () -> service.register(new RegisterDtos.RegisterRequest("john", "p", "p", Set.of("USER"))));
            verify(usuarioRepository, never()).save(any());
        }

        @Test
        void deveLancarQuandoRoleInvalida() {
            when(usuarioRepository.findByUsername("john")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> service.register(new RegisterDtos.RegisterRequest("john", "p", "p", Set.of("invalid_role"))));
            verify(usuarioRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("register: deve salvar com senha codificada e roles mapeadas")
    void register_success() {
        String username = "newuser";
        String raw = "pass";
        String enc = "{bcrypt}encoded";

        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(raw)).thenReturn(enc);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        RegisterDtos.RegisterRequest req = new RegisterDtos.RegisterRequest(
                username, raw, raw, Set.of("LEITURA", "ESCRITA")
        );

        Usuario saved = service.register(req);

        assertEquals(username, saved.getUsername());
        assertEquals(enc, saved.getPassword());
        assertEquals(Set.of(Role.LEITURA, Role.ESCRITA), saved.getRoles());

        ArgumentCaptor<Usuario> cap = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(cap.capture());
        Usuario toPersist = cap.getValue();
        assertEquals(username, toPersist.getUsername());
        assertEquals(enc, toPersist.getPassword());
        assertEquals(Set.of(Role.LEITURA, Role.ESCRITA), toPersist.getRoles());
    }
}
