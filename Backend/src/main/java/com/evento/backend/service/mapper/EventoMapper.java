package com.evento.backend.service.mapper;

import com.evento.backend.domain.Evento;
import com.evento.backend.domain.EventoTipo;
import com.evento.backend.service.dto.EventoDTO;
import com.evento.backend.service.dto.EventoTipoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Evento} and its DTO {@link EventoDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventoMapper extends EntityMapper<EventoDTO, Evento> {
    @Mapping(target = "eventoTipo", source = "eventoTipo", qualifiedByName = "eventoTipoNombre")
    EventoDTO toDto(Evento s);

    @Named("eventoTipoNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    EventoTipoDTO toDtoEventoTipoNombre(EventoTipo eventoTipo);
}
