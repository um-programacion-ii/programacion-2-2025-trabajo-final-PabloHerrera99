package com.evento.backend.service.mapper;

import com.evento.backend.domain.AsientoSeleccionado;
import com.evento.backend.domain.Evento;
import com.evento.backend.service.dto.AsientoVentaDTO;
import com.evento.backend.service.dto.RealizarVentaRequestDTO;
import org.mapstruct.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Mapper para transformar entidades del dominio a DTOs de comunicación
 * con el servidor de cátedra (API endpoints).
 */
@Mapper(componentModel = "spring")
public interface VentaCatedraMapper {

    /**
     * Convierte AsientoSeleccionado (entidad JPA) a AsientoVentaDTO
     * (formato esperado por API de cátedra).
     * 
     * @param asiento Asiento seleccionado en el sistema local
     * @return DTO con formato de API cátedra (fila, columna, persona)
     */
    @Mapping(target = "fila", source = "fila")
    @Mapping(target = "columna", source = "columna")
    @Mapping(target = "persona", source = "nombrePersona")
    AsientoVentaDTO toAsientoVentaDTO(AsientoSeleccionado asiento);

    /**
     * Convierte lista completa de asientos seleccionados.
     */
    List<AsientoVentaDTO> toAsientoVentaDTOList(List<AsientoSeleccionado> asientos);

    /**
     * Construye request completo para endpoint de cátedra POST /realizar-venta.
     * 
     * NOTA: Este método usa default implementation porque MapStruct tiene limitaciones
     * con múltiples parámetros source que no son entidades.
     * 
     * @param evento Evento de la venta (debe tener idCatedra NO nulo)
     * @param precioTotal Precio total calculado
     * @param asientos Asientos vendidos con nombres asignados
     * @return Request DTO listo para enviar a cátedra
     */
    default RealizarVentaRequestDTO toRealizarVentaRequest(
        Evento evento,
        BigDecimal precioTotal,
        List<AsientoSeleccionado> asientos
    ) {
        if (evento == null || evento.getIdCatedra() == null) {
            throw new IllegalArgumentException("Evento debe tener idCatedra válido");
        }
        
        RealizarVentaRequestDTO request = new RealizarVentaRequestDTO();
        request.setEventoId(evento.getIdCatedra());
        request.setFecha(java.time.Instant.now());
        request.setPrecioVenta(precioTotal);
        request.setAsientos(toAsientoVentaDTOList(asientos));
        return request;
    }
}
