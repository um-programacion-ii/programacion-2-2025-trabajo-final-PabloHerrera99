package com.evento.backend.service.mapper;

import com.evento.backend.domain.EventoTipo;
import com.evento.backend.service.dto.EventoTipoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EventoTipo} and its DTO {@link EventoTipoDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventoTipoMapper extends EntityMapper<EventoTipoDTO, EventoTipo> {}
