package com.evento.backend.service.mapper;

import com.evento.backend.domain.AsientoSeleccionado;
import com.evento.backend.domain.Sesion;
import com.evento.backend.service.dto.AsientoSeleccionadoDTO;
import com.evento.backend.service.dto.SesionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AsientoSeleccionado} and its DTO {@link AsientoSeleccionadoDTO}.
 */
@Mapper(componentModel = "spring")
public interface AsientoSeleccionadoMapper extends EntityMapper<AsientoSeleccionadoDTO, AsientoSeleccionado> {
    @Mapping(target = "sesion", source = "sesion", qualifiedByName = "sesionId")
    AsientoSeleccionadoDTO toDto(AsientoSeleccionado s);

    @Named("sesionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SesionDTO toDtoSesionId(Sesion sesion);
}
