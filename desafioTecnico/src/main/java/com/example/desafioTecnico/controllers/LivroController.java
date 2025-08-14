package com.example.desafioTecnico.controllers;

import com.example.desafioTecnico.config.ApiResponse;
import com.example.desafioTecnico.models.dto.LivroCadastroDTO;
import com.example.desafioTecnico.models.dto.LivroResponseDTO;
import com.example.desafioTecnico.services.LivroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.net.URI;


@RestController
@RequestMapping("/api/v1/livros")
public class LivroController {
    private final LivroService service;


    public LivroController(LivroService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LivroResponseDTO>> cadastrar(@Valid @RequestBody LivroCadastroDTO dto){
        LivroResponseDTO created = service.cadastrar(dto);
        return ResponseEntity.created(URI.create("/api/v1/genres/" ))
                .body(ApiResponse.created(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LivroResponseDTO>> buscar(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.ok(service.buscar(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LivroResponseDTO>>> listar(){
        return ResponseEntity.ok(ApiResponse.ok(service.listar()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LivroResponseDTO>> atualizar(@PathVariable Long id, @Valid @RequestBody LivroCadastroDTO dto){
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletar(@PathVariable Long id){
        service.deletar(id);
        return ResponseEntity.status(204).body(ApiResponse.noContent());
    }
}
