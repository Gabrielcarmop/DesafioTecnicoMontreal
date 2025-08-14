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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock private LivroRepository livroRepository;
    @Mock private AutorRepository autorRepository;
    @Mock private GeneroRepository generoRepository;
    @Mock private ModelMapper mapper;

    @InjectMocks
    private LivroService service;

    private Autor autor;
    private Genero genero;

    @BeforeEach
    void setUp() {
        autor = new Autor();
        try {
            var idField = Autor.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(autor, 1L);
        } catch (Exception ignored) {}

        genero = new Genero();
        try {
            var idField = Genero.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(genero, 2L);
        } catch (Exception ignored) {}
    }


    @Test
    void cadastrar_deveSalvarELancarResponse_quandoDadosValidos() {
        LivroCadastroDTO dto = mock(LivroCadastroDTO.class);
        when(dto.getIsbn()).thenReturn("ISBN-123");
        when(dto.getAutorId()).thenReturn(1L);
        when(dto.getGeneroId()).thenReturn(2L);

        when(livroRepository.existsByIsbnIgnoreCase("ISBN-123")).thenReturn(false);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(generoRepository.findById(2L)).thenReturn(Optional.of(genero));

        Livro livroParaSalvar = new Livro();
        when(mapper.map(dto, Livro.class)).thenReturn(livroParaSalvar);

        Livro livroSalvo = new Livro();
        try {
            var idField = Livro.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(livroSalvo, 10L);
        } catch (Exception ignored) {}
        when(livroRepository.save(livroParaSalvar)).thenReturn(livroSalvo);

        LivroResponseDTO esperado = mock(LivroResponseDTO.class);
        when(mapper.map(livroSalvo, LivroResponseDTO.class)).thenReturn(esperado);

        LivroResponseDTO resp = service.cadastrar(dto);

        assertSame(esperado, resp);
        assertSame(autor, livroParaSalvar.getAutor());
        assertSame(genero, livroParaSalvar.getGenero());
        verify(livroRepository).existsByIsbnIgnoreCase("ISBN-123");
        verify(livroRepository).save(livroParaSalvar);
    }

    @Test
    void cadastrar_deveLancarIsbnExistente_quandoIsbnJaExiste() {
        LivroCadastroDTO dto = mock(LivroCadastroDTO.class);
        when(dto.getIsbn()).thenReturn("DUP");
        when(livroRepository.existsByIsbnIgnoreCase("DUP")).thenReturn(true);

        assertThrows(IsbnExistenteException.class, () -> service.cadastrar(dto));
        verify(livroRepository, never()).save(any());
    }


    @Test
    void cadastrar_deveLancarGeneroNaoEncontrado_quandoGeneroInexistente() {
        LivroCadastroDTO dto = mock(LivroCadastroDTO.class);
        when(dto.getIsbn()).thenReturn("ISBN-1");
        when(dto.getAutorId()).thenReturn(1L);
        when(dto.getGeneroId()).thenReturn(999L);

        when(livroRepository.existsByIsbnIgnoreCase("ISBN-1")).thenReturn(false);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(generoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(GeneroNaoEncontradoException.class, () -> service.cadastrar(dto));
        verify(livroRepository, never()).save(any());
    }


    @Test
    void buscar_deveRetornarDTO_quandoEncontrado() {
        Livro livro = new Livro();
        try {
            var idField = Livro.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(livro, 30L);
        } catch (Exception ignored) {}

        when(livroRepository.findById(30L)).thenReturn(Optional.of(livro));

        LivroResponseDTO esperado = mock(LivroResponseDTO.class);
        when(mapper.map(livro, LivroResponseDTO.class)).thenReturn(esperado);

        LivroResponseDTO resp = service.buscar(30L);

        assertSame(esperado, resp);
    }

    @Test
    void buscar_deveLancarNaoEncontrado_quandoInexistente() {
        when(livroRepository.findById(77L)).thenReturn(Optional.empty());
        assertThrows(LivroNaoEncontradoException.class, () -> service.buscar(77L));
    }


    @Test
    void listar_deveMapearTodosLivrosParaDTO() {
        Livro l1 = new Livro();
        Livro l2 = new Livro();

        when(livroRepository.findAll()).thenReturn(List.of(l1, l2));

        LivroResponseDTO r1 = mock(LivroResponseDTO.class);
        LivroResponseDTO r2 = mock(LivroResponseDTO.class);
        when(mapper.map(l1, LivroResponseDTO.class)).thenReturn(r1);
        when(mapper.map(l2, LivroResponseDTO.class)).thenReturn(r2);

        List<LivroResponseDTO> resp = service.listar();

        assertEquals(2, resp.size());
        assertSame(r1, resp.get(0));
        assertSame(r2, resp.get(1));
        verify(livroRepository).findAll();
    }


    @Test
    void atualizar_deveAtualizarELançarResponse_quandoDadosValidos() {
        Long id = 50L;

        Livro existente = new Livro();
        existente.setAutor(new Autor());
        existente.setGenero(new Genero());
        when(livroRepository.findById(id)).thenReturn(Optional.of(existente));

        LivroCadastroDTO dto = mock(LivroCadastroDTO.class);
        when(dto.getIsbn()).thenReturn("NOVO-ISBN");
        when(dto.getAutorId()).thenReturn(1L);
        when(dto.getGeneroId()).thenReturn(2L);

        when(livroRepository.existsByIsbnIgnoreCaseAndIdNot("NOVO-ISBN", id)).thenReturn(false);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(generoRepository.findById(2L)).thenReturn(Optional.of(genero));

        doAnswer(inv -> {
            return null;
        }).when(mapper).map(dto, existente);

        when(livroRepository.save(existente)).thenReturn(existente);

        LivroResponseDTO esperado = mock(LivroResponseDTO.class);
        when(mapper.map(existente, LivroResponseDTO.class)).thenReturn(esperado);

        LivroResponseDTO resp = service.atualizar(id, dto);

        assertSame(esperado, resp);
        assertSame(autor, existente.getAutor());
        assertSame(genero, existente.getGenero());
    }

    @Test
    void atualizar_deveLancarNaoEncontrado_quandoLivroInexistente() {
        LivroCadastroDTO dto = mock(LivroCadastroDTO.class);
        when(livroRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(LivroNaoEncontradoException.class, () -> service.atualizar(999L, dto));
    }

    @Test
    void atualizar_deveLancarIsbnExistente_quandoOutroLivroJaTemMesmoIsbn() {
        Long id = 10L;
        LivroCadastroDTO dto = mock(LivroCadastroDTO.class);
        when(dto.getIsbn()).thenReturn("DUP");

        when(livroRepository.findById(id)).thenReturn(Optional.of(new Livro()));
        when(livroRepository.existsByIsbnIgnoreCaseAndIdNot("DUP", id)).thenReturn(true);

        assertThrows(IsbnExistenteException.class, () -> service.atualizar(id, dto));
        verify(livroRepository, never()).save(any());
    }


    @Test
    void atualizar_deveLancarGeneroNaoEncontrado_quandoGeneroInexistente() {
        Long id = 10L;
        LivroCadastroDTO dto = mock(LivroCadastroDTO.class);
        when(dto.getIsbn()).thenReturn("OK");
        when(dto.getAutorId()).thenReturn(1L);
        when(dto.getGeneroId()).thenReturn(999L);

        when(livroRepository.findById(id)).thenReturn(Optional.of(new Livro()));
        when(livroRepository.existsByIsbnIgnoreCaseAndIdNot("OK", id)).thenReturn(false);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(generoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(GeneroNaoEncontradoException.class, () -> service.atualizar(id, dto));
        verify(livroRepository, never()).save(any());
    }


    @Test
    void deletar_deveExcluir_quandoExiste() {
        when(livroRepository.existsById(123L)).thenReturn(true);

        service.deletar(123L);

        verify(livroRepository).deleteById(123L);
    }

    @Test
    void deletar_deveLancarNaoEncontrado_quandoInexistente() {
        when(livroRepository.existsById(321L)).thenReturn(false);

        assertThrows(LivroNaoEncontradoException.class, () -> service.deletar(321L));
        verify(livroRepository, never()).deleteById(anyLong());
    }
}
