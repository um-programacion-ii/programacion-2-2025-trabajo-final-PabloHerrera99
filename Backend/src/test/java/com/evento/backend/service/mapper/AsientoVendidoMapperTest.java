package com.evento.backend.service.mapper;

import static com.evento.backend.domain.AsientoVendidoAsserts.*;
import static com.evento.backend.domain.AsientoVendidoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AsientoVendidoMapperTest {

    private AsientoVendidoMapper asientoVendidoMapper;

    @BeforeEach
    void setUp() {
        asientoVendidoMapper = new AsientoVendidoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAsientoVendidoSample1();
        var actual = asientoVendidoMapper.toEntity(asientoVendidoMapper.toDto(expected));
        assertAsientoVendidoAllPropertiesEquals(expected, actual);
    }
}
