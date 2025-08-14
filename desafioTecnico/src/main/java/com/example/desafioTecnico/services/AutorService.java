package com.example.desafioTecnico.services;

import com.example.desafioTecnico.exception.AutorExistenteException;
import com.example.desafioTecnico.exception.AutorNaoEncontradoException;
import com.example.desafioTecnico.models.dto.AutorCadastroDTO;
import com.example.desafioTecnico.models.dto.AutorResponseDTO;
import com.example.desafioTecnico.models.entities.Autor;
import com.example.desafioTecnico.repositories.AutorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AutorService {

    private final AutorRepository repository;
    private final ModelMapper mapper;

    public AutorService(AutorRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public AutorResponseDTO cadastrar(AutorCadastroDTO dto) {
        if (repository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new AutorExistenteException("Autor já existe: " + dto.getNome());
        }

        Autor autorCadastro = mapper.map(dto, Autor.class);
        Autor saved = repository.save(autorCadastro);
        return mapper.map(saved, AutorResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public AutorResponseDTO buscar(Long id) {
        Autor autor = repository.findById(id)
                .orElseThrow(() -> new AutorNaoEncontradoException("Autor não encontrado com ID: " + id));
        return mapper.map(autor, AutorResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public List<AutorResponseDTO> listar() {
        return repository.findAll().stream()
                .map(autor -> mapper.map(autor, AutorResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public AutorResponseDTO atualizar(Long id, AutorCadastroDTO dto) {
        Autor autor = repository.findById(id)
                .orElseThrow(() -> new AutorNaoEncontradoException("Autor não encontrado com ID: " + id));


        mapper.map(dto, autor);
        Autor autorAtualizado = repository.save(autor);
        return mapper.map(autorAtualizado, AutorResponseDTO.class);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new AutorNaoEncontradoException("Autor não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}