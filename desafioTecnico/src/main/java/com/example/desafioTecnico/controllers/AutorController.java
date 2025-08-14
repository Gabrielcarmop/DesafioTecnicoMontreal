package com.example.desafioTecnico.controllers;

import com.example.desafioTecnico.config.ApiResponse;
import com.example.desafioTecnico.models.dto.AutorCadastroDTO;
import com.example.desafioTecnico.models.dto.AutorResponseDTO;
import com.example.desafioTecnico.services.AutorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/autores")
public class AutorController {
    private final AutorService service;

    public AutorController(AutorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AutorResponseDTO>> cadastrar(@Valid @RequestBody AutorCadastroDTO dto){
        AutorResponseDTO created = service.cadastrar(dto);
        return ResponseEntity.created(URI.create("/api/v1/authors/" ))
                .body(ApiResponse.created(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AutorResponseDTO>> buscar(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.ok(service.buscar(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AutorResponseDTO>>> listar(){
        return ResponseEntity.ok(ApiResponse.ok(service.listar()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AutorResponseDTO>> atualizar(@PathVariable Long id, @Valid @RequestBody AutorCadastroDTO dto){
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletar(@PathVariable Long id){
        service.deletar(id);
        return ResponseEntity.status(204).body(ApiResponse.noContent());
    }
}
