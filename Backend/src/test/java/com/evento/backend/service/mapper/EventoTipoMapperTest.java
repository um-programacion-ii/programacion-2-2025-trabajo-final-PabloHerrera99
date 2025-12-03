package com.evento.backend.service.mapper;

import static com.evento.backend.domain.EventoTipoAsserts.*;
import static com.evento.backend.domain.EventoTipoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventoTipoMapperTest {

    private EventoTipoMapper eventoTipoMapper;

    @BeforeEach
    void setUp() {
        eventoTipoMapper = new EventoTipoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEventoTipoSample1();
        var actual = eventoTipoMapper.toEntity(eventoTipoMapper.toDto(expected));
        assertEventoTipoAllPropertiesEquals(expected, actual);
    }
}
