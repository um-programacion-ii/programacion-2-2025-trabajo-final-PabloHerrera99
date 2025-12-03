package com.evento.backend.service.mapper;

import static com.evento.backend.domain.AsientoSeleccionadoAsserts.*;
import static com.evento.backend.domain.AsientoSeleccionadoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AsientoSeleccionadoMapperTest {

    private AsientoSeleccionadoMapper asientoSeleccionadoMapper;

    @BeforeEach
    void setUp() {
        asientoSeleccionadoMapper = new AsientoSeleccionadoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAsientoSeleccionadoSample1();
        var actual = asientoSeleccionadoMapper.toEntity(asientoSeleccionadoMapper.toDto(expected));
        assertAsientoSeleccionadoAllPropertiesEquals(expected, actual);
    }
}
