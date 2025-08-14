package com.example.desafioTecnico.services;

import com.example.desafioTecnico.exception.GeneroExistenteException;
import com.example.desafioTecnico.exception.GeneroNaoEncontradoException;
import com.example.desafioTecnico.models.dto.GeneroCadastroDTO;
import com.example.desafioTecnico.models.dto.GeneroResponseDTO;
import com.example.desafioTecnico.models.entities.Genero;
import com.example.desafioTecnico.repositories.GeneroRepository;
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
class GeneroServiceTest {

    @Mock private GeneroRepository repository;
    @Mock private ModelMapper mapper;

    @InjectMocks
    private GeneroService service;

    @Test
    void cadastrar_deveSalvarERetornarDTO_quandoNomeDisponivel() {
        GeneroCadastroDTO dto = mock(GeneroCadastroDTO.class);
        when(dto.getNome()).thenReturn("Ficção");

        when(repository.existsByNomeIgnoreCase("Ficção")).thenReturn(false);

        Genero entidadeMapeada = new Genero();
        when(mapper.map(dto, Genero.class)).thenReturn(entidadeMapeada);

        Genero salvo = new Genero();
        when(repository.save(entidadeMapeada)).thenReturn(salvo);

        GeneroResponseDTO esperado = mock(GeneroResponseDTO.class);
        when(mapper.map(salvo, GeneroResponseDTO.class)).thenReturn(esperado);

        GeneroResponseDTO resp = service.cadastrar(dto);

        assertSame(esperado, resp);
        verify(repository).existsByNomeIgnoreCase("Ficção");
        verify(repository).save(entidadeMapeada);
    }

    @Test
    void cadastrar_deveLancarGeneroExistente_quandoNomeDuplicado() {
        GeneroCadastroDTO dto = mock(GeneroCadastroDTO.class);
        when(dto.getNome()).thenReturn("Ficção");

        when(repository.existsByNomeIgnoreCase("Ficção")).thenReturn(true);

        assertThrows(GeneroExistenteException.class, () -> service.cadastrar(dto));
        verify(repository, never()).save(any());
    }

    @Test
    void buscar_deveRetornarDTO_quandoEncontrado() {
        Genero genero = new Genero();
        when(repository.findById(10L)).thenReturn(Optional.of(genero));

        GeneroResponseDTO esperado = mock(GeneroResponseDTO.class);
        when(mapper.map(genero, GeneroResponseDTO.class)).thenReturn(esperado);

        GeneroResponseDTO resp = service.buscar(10L);

        assertSame(esperado, resp);
        verify(repository).findById(10L);
    }

    @Test
    void buscar_deveLancarNaoEncontrado_quandoInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(GeneroNaoEncontradoException.class, () -> service.buscar(99L));
    }

    @Test
    void listar_deveMapearListaParaDTO() {
        Genero g1 = new Genero();
        Genero g2 = new Genero();
        when(repository.findAll()).thenReturn(List.of(g1, g2));

        GeneroResponseDTO r1 = mock(GeneroResponseDTO.class);
        GeneroResponseDTO r2 = mock(GeneroResponseDTO.class);
        when(mapper.map(g1, GeneroResponseDTO.class)).thenReturn(r1);
        when(mapper.map(g2, GeneroResponseDTO.class)).thenReturn(r2);

        List<GeneroResponseDTO> resp = service.listar();

        assertEquals(2, resp.size());
        assertSame(r1, resp.get(0));
        assertSame(r2, resp.get(1));
        verify(repository).findAll();
    }


    @Test
    void atualizar_deveAtualizarESalvar_quandoEncontrado() {
        Long id = 5L;

        Genero existente = new Genero();
        when(repository.findById(id)).thenReturn(Optional.of(existente));

        GeneroCadastroDTO dto = mock(GeneroCadastroDTO.class);

        doAnswer(inv -> null).when(mapper).map(dto, existente);

        when(repository.save(existente)).thenReturn(existente);

        GeneroResponseDTO esperado = mock(GeneroResponseDTO.class);
        when(mapper.map(existente, GeneroResponseDTO.class)).thenReturn(esperado);

        GeneroResponseDTO resp = service.atualizar(id, dto);

        assertSame(esperado, resp);
        verify(mapper).map(dto, existente);
        verify(repository).save(existente);
    }

    @Test
    void atualizar_deveLancarNaoEncontrado_quandoInexistente() {
        when(repository.findById(77L)).thenReturn(Optional.empty());

        GeneroCadastroDTO dto = mock(GeneroCadastroDTO.class);
        assertThrows(GeneroNaoEncontradoException.class, () -> service.atualizar(77L, dto));
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

        assertThrows(GeneroNaoEncontradoException.class, () -> service.deletar(4L));
        verify(repository, never()).deleteById(anyLong());
    }
}
