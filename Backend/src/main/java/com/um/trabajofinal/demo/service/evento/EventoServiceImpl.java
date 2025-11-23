package com.um.trabajofinal.demo.service.evento;

import com.um.trabajofinal.demo.persistence.domain.Evento;
import com.um.trabajofinal.demo.persistence.repository.EventoRepository;
import com.um.trabajofinal.demo.api.dto.EventoResumeDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoServiceImpl implements EventoService {

    private final EventoRepository eventoRepository;

    public EventoServiceImpl(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Override
    public List<EventoResumeDto> getEventosResumidos() {
        return eventoRepository.findAll().stream()
                .map(e -> EventoResumeDto.builder()
                        .id(e.getId())
                        .nombre(e.getNombre())
                        .fechaHora(e.getFechaHora())
                        .precioBase(e.getPrecioBase())
                        .activo(e.isActivo())
                        .build())
                .collect(Collectors.toList());
    }
}
