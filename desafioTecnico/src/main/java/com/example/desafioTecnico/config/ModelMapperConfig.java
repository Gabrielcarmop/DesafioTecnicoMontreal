package com.example.desafioTecnico.config;

import com.example.desafioTecnico.models.dto.LivroCadastroDTO;
import com.example.desafioTecnico.models.entities.Livro;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        mapper.typeMap(LivroCadastroDTO.class, Livro.class)
                .addMappings(m -> {
                    m.skip(Livro::setId);
                    m.skip(Livro::setAutor);
                    m.skip(Livro::setGenero);
                });

        return mapper;
    }
}
