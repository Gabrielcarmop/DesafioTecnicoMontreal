package com.example.desafioTecnico.services;

import com.example.desafioTecnico.exception.AutorExistenteException;
import com.example.desafioTecnico.exception.AutorNaoEncontradoException;
import com.example.desafioTecnico.models.dto.AutorCadastroDTO;
import com.example.desafioTecnico.models.dto.AutorResponseDTO;
import com.example.desafioTecnico.models.entities.Autor;
import com.example.desafioTecnico.repositories.AutorRepository;
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
class AutorServiceTest {

    @Mock private AutorRepository repository;
    @Mock private ModelMapper mapper;

    @InjectMocks
    private AutorService service;

    @Test
    void cadastrar_deveSalvarERetornarDTO_quandoNomeDisponivel() {
        AutorCadastroDTO dto = mock(AutorCadastroDTO.class);
        when(dto.getNome()).thenReturn("Arthur C. Clarke");

        when(repository.existsByNomeIgnoreCase("Arthur C. Clarke")).thenReturn(false);

        Autor entidadeMapeada = new Autor();
        when(mapper.map(dto, Autor.class)).thenReturn(entidadeMapeada);

        Autor salvo = new Autor();
        when(repository.save(entidadeMapeada)).thenReturn(salvo);

        AutorResponseDTO esperado = mock(AutorResponseDTO.class);
        when(mapper.map(salvo, AutorResponseDTO.class)).thenReturn(esperado);

        AutorResponseDTO resp = service.cadastrar(dto);

        assertSame(esperado, resp);
        verify(repository).existsByNomeIgnoreCase("Arthur C. Clarke");
        verify(repository).save(entidadeMapeada);
    }

    @Test
    void cadastrar_deveLancarAutorExistente_quandoNomeDuplicado() {
        AutorCadastroDTO dto = mock(AutorCadastroDTO.class);
        when(dto.getNome()).thenReturn("Arthur C. Clarke");

        when(repository.existsByNomeIgnoreCase("Arthur C. Clarke")).thenReturn(true);

        assertThrows(AutorExistenteException.class, () -> service.cadastrar(dto));
        verify(repository, never()).save(any());
    }

    @Test
    void buscar_deveRetornarDTO_quandoEncontrado() {
        Autor autor = new Autor();
        when(repository.findById(10L)).thenReturn(Optional.of(autor));

        AutorResponseDTO esperado = mock(AutorResponseDTO.class);
        when(mapper.map(autor, AutorResponseDTO.class)).thenReturn(esperado);

        AutorResponseDTO resp = service.buscar(10L);

        assertSame(esperado, resp);
        verify(repository).findById(10L);
    }

    @Test
    void buscar_deveLancarNaoEncontrado_quandoInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AutorNaoEncontradoException.class, () -> service.buscar(99L));
    }


    @Test
    void listar_deveMapearListaParaDTO() {
        Autor a1 = new Autor();
        Autor a2 = new Autor();
        when(repository.findAll()).thenReturn(List.of(a1, a2));

        AutorResponseDTO r1 = mock(AutorResponseDTO.class);
        AutorResponseDTO r2 = mock(AutorResponseDTO.class);
        when(mapper.map(a1, AutorResponseDTO.class)).thenReturn(r1);
        when(mapper.map(a2, AutorResponseDTO.class)).thenReturn(r2);

        List<AutorResponseDTO> resp = service.listar();

        assertEquals(2, resp.size());
        assertSame(r1, resp.get(0));
        assertSame(r2, resp.get(1));
        verify(repository).findAll();
    }

    @Test
    void atualizar_deveAtualizarESalvar_quandoEncontrado() {
        Long id = 5L;

        Autor existente = new Autor();
        when(repository.findById(id)).thenReturn(Optional.of(existente));

        AutorCadastroDTO dto = mock(AutorCadastroDTO.class);

        // mapper.map(dto, existente) não retorna valor
        doAnswer(inv -> null).when(mapper).map(dto, existente);

        when(repository.save(existente)).thenReturn(existente);

        AutorResponseDTO esperado = mock(AutorResponseDTO.class);
        when(mapper.map(existente, AutorResponseDTO.class)).thenReturn(esperado);

        AutorResponseDTO resp = service.atualizar(id, dto);

        assertSame(esperado, resp);
        verify(mapper).map(dto, existente);
        verify(repository).save(existente);
    }

    @Test
    void atualizar_deveLancarNaoEncontrado_quandoInexistente() {
        when(repository.findById(77L)).thenReturn(Optional.empty());

        AutorCadastroDTO dto = mock(AutorCadastroDTO.class);
        assertThrows(AutorNaoEncontradoException.class, () -> service.atualizar(77L, dto));
        verify(repository, never()).save(any());
    }

    @Test
    void deletar_deveExcluir_quandoExiste() {
        when(repository.existsById(3L)).thenReturn(true);

        service.deletar(3L);

        verify(repository).deleteById(3L);
    }

    @Test
    void deletar_deveLancarNaoEncontrado_quandoInexistente() {
        when(repository.existsById(4L)).thenReturn(false);

        assertThrows(AutorNaoEncontradoException.class, () -> service.deletar(4L));
        verify(repository, never()).deleteById(anyLong());
    }
}
