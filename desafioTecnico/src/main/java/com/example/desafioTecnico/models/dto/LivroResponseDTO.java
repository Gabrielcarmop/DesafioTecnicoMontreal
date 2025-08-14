package com.example.desafioTecnico.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LivroResponseDTO {
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 255, message = "O título deve ter no máximo 255 caracteres")
    private String titulo;

    @NotBlank(message = "O ISBN é obrigatório")
    @Size(max = 20, message = "O ISBN deve ter no máximo 20 caracteres")
    private String isbn;

    @NotBlank(message = "A editora é obrigatória")
    @Size(max = 20, message = "A editora deve ter no máximo 20 caracteres")
    private String editora;

    private Integer anoPublicacao;

    @NotNull(message = "O ID do gênero é obrigatório")
    private GeneroResponseDTO genero;

    @NotNull(message = "O ID do autor é obrigatório")
    private AutorResponseDTO autor;
}
