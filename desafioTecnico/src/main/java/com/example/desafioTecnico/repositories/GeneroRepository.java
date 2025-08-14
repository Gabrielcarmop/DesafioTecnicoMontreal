package com.example.desafioTecnico.repositories;

import com.example.desafioTecnico.models.entities.Genero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeneroRepository extends JpaRepository<Genero, Long> {
    boolean existsByNomeIgnoreCase(String nome);
}
