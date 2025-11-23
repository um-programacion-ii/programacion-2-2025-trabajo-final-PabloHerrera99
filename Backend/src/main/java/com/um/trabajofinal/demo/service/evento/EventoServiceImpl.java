package com.um.trabajofinal.demo.service.evento;

import com.um.trabajofinal.demo.api.dto.EventoDetailDto;
import com.um.trabajofinal.demo.persistence.domain.Evento;
import com.um.trabajofinal.demo.persistence.repository.AsientoVentaRepository;
import com.um.trabajofinal.demo.persistence.repository.EventoRepository;
import com.um.trabajofinal.demo.api.dto.EventoResumeDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventoServiceImpl implements EventoService {

    private final EventoRepository eventoRepository;
    private final AsientoVentaRepository asientoVentaRepository;

    public EventoServiceImpl(EventoRepository eventoRepository, 
                           AsientoVentaRepository asientoVentaRepository) {
        this.eventoRepository = eventoRepository;
        this.asientoVentaRepository = asientoVentaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventoResumeDto> getEventosResumidos() {
        return eventoRepository.findByActivoTrue().stream()
                .map(this::mapToResumeDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EventoDetailDto> getEventoById(Long id) {
        return eventoRepository.findById(id)
                .map(this::mapToDetailDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEventoActive(Long id) {
        return eventoRepository.findById(id)
                .map(evento -> evento.isActivo() && evento.getFechaHora().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EventoDetailDto> getEventoByExternalId(String externalId) {
        return eventoRepository.findByExternalId(externalId)
                .map(this::mapToDetailDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventoResumeDto> getAllEventos() {
        return eventoRepository.findAll().stream()
                .map(this::mapToResumeDto)
                .collect(Collectors.toList());
    }

    // Métodos de mapeo privados
    
    private EventoResumeDto mapToResumeDto(Evento evento) {
        return EventoResumeDto.builder()
                .id(evento.getId())
                .nombre(evento.getNombre())
                .fechaHora(evento.getFechaHora())
                .precioBase(evento.getPrecioBase())
                .activo(evento.isActivo())
                .build();
    }

    private EventoDetailDto mapToDetailDto(Evento evento) {
        // Calcular asientos disponibles (layout fijo 10x10 = 100 asientos)
        int totalAsientos = 100; // A1-J10
        int asientosVendidos = asientoVentaRepository.findByVentaEventoId(evento.getId()).size();
        int asientosDisponibles = totalAsientos - asientosVendidos;

        return EventoDetailDto.builder()
                .id(evento.getId())
                .externalId(evento.getExternalId())
                .nombre(evento.getNombre())
                .descripcion(evento.getDescripcion())
                .categoria(evento.getCategoria())
                .fechaHora(evento.getFechaHora())
                .lugar(evento.getLugar())
                .sala(evento.getSala())
                .precioBase(evento.getPrecioBase())
                .moneda(evento.getMoneda())
                .activo(evento.isActivo())
                .totalAsientos(totalAsientos)
                .asientosDisponibles(asientosDisponibles)
                .fechaCreacion(null) // fechaCreacion no existe en la entidad
                .fechaModificacion(null) // fechaModificacion no existe en la entidad
                .build();
    }
}
