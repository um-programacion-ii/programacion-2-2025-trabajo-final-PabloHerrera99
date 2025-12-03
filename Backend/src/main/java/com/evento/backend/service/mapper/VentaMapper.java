package com.evento.backend.service.mapper;

import com.evento.backend.domain.Evento;
import com.evento.backend.domain.User;
import com.evento.backend.domain.Venta;
import com.evento.backend.service.dto.EventoDTO;
import com.evento.backend.service.dto.UserDTO;
import com.evento.backend.service.dto.VentaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Venta} and its DTO {@link VentaDTO}.
 */
@Mapper(componentModel = "spring")
public interface VentaMapper extends EntityMapper<VentaDTO, Venta> {
    @Mapping(target = "evento", source = "evento", qualifiedByName = "eventoTitulo")
    @Mapping(target = "usuario", source = "usuario", qualifiedByName = "userLogin")
    VentaDTO toDto(Venta s);

    @Named("eventoTitulo")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    EventoDTO toDtoEventoTitulo(Evento evento);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
