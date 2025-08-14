package com.example.desafioTecnico.services;

import com.example.desafioTecnico.exception.AutorNaoEncontradoException;
import com.example.desafioTecnico.exception.GeneroNaoEncontradoException;
import com.example.desafioTecnico.exception.IsbnExistenteException;
import com.example.desafioTecnico.exception.LivroNaoEncontradoException;
import com.example.desafioTecnico.models.dto.LivroCadastroDTO;
import com.example.desafioTecnico.models.dto.LivroResponseDTO;
import com.example.desafioTecnico.models.entities.Autor;
import com.example.desafioTecnico.models.entities.Genero;
import com.example.desafioTecnico.models.entities.Livro;
import com.example.desafioTecnico.repositories.AutorRepository;
import com.example.desafioTecnico.repositories.GeneroRepository;
import com.example.desafioTecnico.repositories.LivroRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LivroService {

    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;
    private final GeneroRepository generoRepository;
    private final ModelMapper mapper;

    public LivroService(LivroRepository livroRepository,
                        AutorRepository autorRepository,
                        GeneroRepository generoRepository,
                        ModelMapper mapper) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
        this.generoRepository = generoRepository;
        this.mapper = mapper;
    }

    @Transactional
    public LivroResponseDTO cadastrar(LivroCadastroDTO dto) {
        validarIsbnUnico(dto.getIsbn(), null);

        Autor autor = buscarAutor(dto.getAutorId());
        Genero genero = buscarGenero(dto.getGeneroId());

        Livro livro = mapper.map(dto, Livro.class);
        livro.setAutor(autor);
        livro.setGenero(genero);

        Livro livroSalvo = livroRepository.save(livro);
        return mapper.map(livroSalvo, LivroResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public LivroResponseDTO buscar(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new LivroNaoEncontradoException("Livro não encontrado com ID: " + id));
        return mapper.map(livro, LivroResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public List<LivroResponseDTO> listar() {
        return livroRepository.findAll().stream()
                .map(livro -> mapper.map(livro, LivroResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public LivroResponseDTO atualizar(Long id, LivroCadastroDTO dto) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new LivroNaoEncontradoException("Livro não encontrado com ID: " + id));

        validarIsbnUnico(dto.getIsbn(), id);

        Autor autor = buscarAutor(dto.getAutorId());
        Genero genero = buscarGenero(dto.getGeneroId());

        mapper.map(dto, livro);
        livro.setAutor(autor);
        livro.setGenero(genero);

        Livro livroAtualizado = livroRepository.save(livro);
        return mapper.map(livroAtualizado, LivroResponseDTO.class);
    }

    @Transactional
    public void deletar(Long id) {
        if (!livroRepository.existsById(id)) {
            throw new LivroNaoEncontradoException("Livro não encontrado com ID: " + id);
        }
        livroRepository.deleteById(id);
    }

    private void validarIsbnUnico(String isbn, Long id) {
        if (id == null) {
            if (livroRepository.existsByIsbnIgnoreCase(isbn)) {
                throw new IsbnExistenteException("Já existe um livro com este ISBN: " + isbn);
            }
        } else {
            if (livroRepository.existsByIsbnIgnoreCaseAndIdNot(isbn, id)) {
                throw new IsbnExistenteException("Já existe outro livro com este ISBN: " + isbn);
            }
        }
    }

    private Autor buscarAutor(Long autorId) {
        return autorRepository.findById(autorId)
                .orElseThrow(() -> new AutorNaoEncontradoException("Autor não encontrado com ID: " + autorId));
    }

    private Genero buscarGenero(Long generoId) {
        return generoRepository.findById(generoId)
                .orElseThrow(() -> new GeneroNaoEncontradoException("Gênero não encontrado com ID: " + generoId));
    }
}