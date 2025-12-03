package com.evento.backend.service.mapper;

import com.evento.backend.domain.Evento;
import com.evento.backend.domain.Sesion;
import com.evento.backend.domain.User;
import com.evento.backend.service.dto.EventoDTO;
import com.evento.backend.service.dto.SesionDTO;
import com.evento.backend.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Sesion} and its DTO {@link SesionDTO}.
 */
@Mapper(componentModel = "spring")
public interface SesionMapper extends EntityMapper<SesionDTO, Sesion> {
    @Mapping(target = "usuario", source = "usuario", qualifiedByName = "userLogin")
    @Mapping(target = "evento", source = "evento", qualifiedByName = "eventoTitulo")
    SesionDTO toDto(Sesion s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("eventoTitulo")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    EventoDTO toDtoEventoTitulo(Evento evento);
}
