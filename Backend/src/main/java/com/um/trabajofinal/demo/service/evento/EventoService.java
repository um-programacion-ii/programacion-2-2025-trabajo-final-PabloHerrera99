package com.um.trabajofinal.demo.service.evento;

import com.um.trabajofinal.demo.api.dto.EventoDetailDto;
import com.um.trabajofinal.demo.api.dto.EventoResumeDto;

import java.util.List;
import java.util.Optional;

public interface EventoService {
    
    /**
     * Obtener lista resumida de eventos activos
     */
    List<EventoResumeDto> getEventosResumidos();
    
    /**
     * Obtener detalle completo de un evento por ID
     */
    Optional<EventoDetailDto> getEventoById(Long id);
    
    /**
     * Verificar si un evento está activo y disponible para venta
     */
    boolean isEventoActive(Long id);
    
    /**
     * Obtener evento por external ID
     */
    Optional<EventoDetailDto> getEventoByExternalId(String externalId);
    
    /**
     * Obtener todos los eventos (incluyendo inactivos)
     */
    List<EventoResumeDto> getAllEventos();
}
