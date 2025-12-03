package com.evento.backend.service.mapper;

import com.evento.backend.domain.Evento;
import com.evento.backend.domain.Integrante;
import com.evento.backend.service.dto.EventoDTO;
import com.evento.backend.service.dto.IntegranteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Integrante} and its DTO {@link IntegranteDTO}.
 */
@Mapper(componentModel = "spring")
public interface IntegranteMapper extends EntityMapper<IntegranteDTO, Integrante> {
    @Mapping(target = "evento", source = "evento", qualifiedByName = "eventoTitulo")
    IntegranteDTO toDto(Integrante s);

    @Named("eventoTitulo")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    EventoDTO toDtoEventoTitulo(Evento evento);
}
