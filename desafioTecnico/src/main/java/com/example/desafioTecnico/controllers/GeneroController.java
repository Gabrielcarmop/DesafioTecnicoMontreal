package com.example.desafioTecnico.controllers;

import com.example.desafioTecnico.config.ApiResponse;
import com.example.desafioTecnico.models.dto.GeneroCadastroDTO;
import com.example.desafioTecnico.models.dto.GeneroResponseDTO;
import com.example.desafioTecnico.services.GeneroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.net.URI;


@RestController
@RequestMapping("/api/v1/generos")
public class GeneroController {
    private final GeneroService service;

    public GeneroController(GeneroService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GeneroResponseDTO>> cadastrar(
            @Valid @RequestBody GeneroCadastroDTO dto) {

        GeneroResponseDTO created = service.cadastrar(dto);

        URI location = URI.create("/api/v1/generos/" + created.getId());
        return ResponseEntity.created(location)
                .body(ApiResponse.created(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GeneroResponseDTO>> buscar(@PathVariable Long id){
        GeneroResponseDTO genero =  service.buscar(id);
        return ResponseEntity.ok(ApiResponse.ok(genero));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GeneroResponseDTO>>> listar(){
        return ResponseEntity.ok(ApiResponse.ok(service.listar()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GeneroResponseDTO>> atualizar(@PathVariable Long id, @Valid @RequestBody GeneroCadastroDTO dto){
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletar(@PathVariable Long id){
        service.deletar(id);
        return ResponseEntity.status(204).body(ApiResponse.noContent());
    }
}
