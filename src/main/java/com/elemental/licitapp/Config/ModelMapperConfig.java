package com.elemental.licitapp.Config;

import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop.dto.SecopLicitacionDTO;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // 1. Convertidor para la URL (Lógica que tenías en el DTO)
        Converter<Object, String> urlConverter = new AbstractConverter<Object, String>() {
            @Override
            protected String convert(Object source) {
                if (source == null) return null;
                if (source instanceof String) return (String) source;
                if (source instanceof Map) {
                    return (String) ((Map<?, ?>) source).getOrDefault("url", null);
                }
                return source.toString();
            }
        };

        // 2. Convertidor para la Ubicación (Concatenación)
        Converter<SecopLicitacionDTO, String> ubicacionConverter = ctx -> {
            String depto = ctx.getSource().getDepartamentoEntidad();
            String ciudad = ctx.getSource().getCiudadEntidad();
            return (depto != null ? depto : "") + " - " + (ciudad != null ? ciudad : "");
        };

        // 3. Aplicar las reglas
        modelMapper.typeMap(SecopLicitacionDTO.class, Licitacion.class)
                .addMappings(mapper -> {
                    // Mapeo explícito usando el convertidor de URL
                    mapper.using(urlConverter).map(SecopLicitacionDTO::getUrlProceso, Licitacion::setUrlSecop);
                    // Mapeo explícito para la ubicación
                    mapper.using(ubicacionConverter).map(src -> src, Licitacion::setUbicacion);
                });

        return modelMapper;
    }
}
