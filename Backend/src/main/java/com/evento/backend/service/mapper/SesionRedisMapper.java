package com.evento.backend.service.mapper;

import com.evento.backend.service.dto.SesionDTO;
import com.evento.backend.service.dto.SesionRedisDTO;
import org.mapstruct.*;
import java.time.Instant;

/**
 * Mapper para transformaciones entre SesionDTO y SesionRedisDTO.
 * Usado para almacenamiento temporal en Redis.
 */
@Mapper(componentModel = "spring")
public interface SesionRedisMapper {

    /**
     * Convierte SesionDTO a SesionRedisDTO para guardar en Redis.
     * 
     * @param sesionDTO DTO de sesión persistente (PostgreSQL)
     * @return DTO optimizado para Redis con TTL calculado
     */
    @Mapping(target = "sesionId", source = "id")
    @Mapping(target = "eventoId", source = "evento.id")
    @Mapping(target = "userId", source = "usuario.id", qualifiedByName = "userIdToString")
    @Mapping(target = "estado", source = "estado")
    @Mapping(target = "ultimaActividad", source = "ultimaActividad")
    @Mapping(target = "expiracion", expression = "java(calculateExpiration())")
    @Mapping(target = "asientosSeleccionados", ignore = true) // Manejado por lógica de negocio
    @Mapping(target = "nombresAsignados", ignore = true)      // Manejado por lógica de negocio
    SesionRedisDTO toRedisDTO(SesionDTO sesionDTO);

    /**
     * Convierte ID de usuario (Long) a String para almacenamiento en Redis.
     */
    @Named("userIdToString")
    default String userIdToString(Long userId) {
        return userId != null ? userId.toString() : null;
    }

    /**
     * Calcula timestamp de expiración (30 minutos desde ahora).
     * Usado para TTL de sesiones en Redis.
     */
    default Instant calculateExpiration() {
        return Instant.now().plusSeconds(30 * 60);
    }
}
