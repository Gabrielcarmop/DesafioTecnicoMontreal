package com.example.desafioTecnico.services;

import com.example.desafioTecnico.exception.GeneroExistenteException;
import com.example.desafioTecnico.exception.GeneroNaoEncontradoException;
import com.example.desafioTecnico.models.dto.GeneroCadastroDTO;
import com.example.desafioTecnico.models.dto.GeneroResponseDTO;
import com.example.desafioTecnico.models.entities.Genero;
import com.example.desafioTecnico.repositories.GeneroRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GeneroService {

    private final GeneroRepository repository;
    private final ModelMapper mapper;

    public GeneroService(GeneroRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public GeneroResponseDTO cadastrar(GeneroCadastroDTO dto) {
        if (repository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new GeneroExistenteException("Já existe um gênero com este nome: " + dto.getNome());
        }

        Genero genero = mapper.map(dto, Genero.class);
        Genero saved = repository.save(genero);
        return mapper.map(saved, GeneroResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public GeneroResponseDTO buscar(Long id) {
        Genero genero = repository.findById(id)
                .orElseThrow(() -> new GeneroNaoEncontradoException("Gênero não encontrado com ID: " + id));
        return mapper.map(genero, GeneroResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public List<GeneroResponseDTO> listar() {
        return repository.findAll().stream()
                .map(genero -> mapper.map(genero, GeneroResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public GeneroResponseDTO atualizar(Long id, GeneroCadastroDTO dto) {
        Genero genero = repository.findById(id)
                .orElseThrow(() -> new GeneroNaoEncontradoException("Gênero não encontrado com ID: " + id));


        mapper.map(dto, genero);
        Genero updated = repository.save(genero);
        return mapper.map(updated, GeneroResponseDTO.class);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new GeneroNaoEncontradoException("Gênero não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}