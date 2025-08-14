package com.example.desafioTecnico.repositories;

import com.example.desafioTecnico.models.entities.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    boolean existsByNomeIgnoreCase(String nome);
}
