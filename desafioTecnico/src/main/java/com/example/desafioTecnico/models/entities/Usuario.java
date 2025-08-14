package com.example.desafioTecnico.models.entities;

import com.example.desafioTecnico.models.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public Set<String> getRolesAsString() {
        return roles == null ? Set.of() : roles.stream().map(Enum::name).collect(Collectors.toSet());
    }

}
