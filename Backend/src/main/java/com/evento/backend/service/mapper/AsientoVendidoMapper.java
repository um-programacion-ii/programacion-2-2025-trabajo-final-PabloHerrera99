package com.evento.backend.service.mapper;

import com.evento.backend.domain.AsientoVendido;
import com.evento.backend.domain.Venta;
import com.evento.backend.service.dto.AsientoVendidoDTO;
import com.evento.backend.service.dto.VentaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AsientoVendido} and its DTO {@link AsientoVendidoDTO}.
 */
@Mapper(componentModel = "spring")
public interface AsientoVendidoMapper extends EntityMapper<AsientoVendidoDTO, AsientoVendido> {
    @Mapping(target = "venta", source = "venta", qualifiedByName = "ventaId")
    AsientoVendidoDTO toDto(AsientoVendido s);

    @Named("ventaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    VentaDTO toDtoVentaId(Venta venta);
}
