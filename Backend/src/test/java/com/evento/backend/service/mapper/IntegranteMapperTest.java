package com.evento.backend.service.mapper;

import static com.evento.backend.domain.IntegranteAsserts.*;
import static com.evento.backend.domain.IntegranteTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IntegranteMapperTest {

    private IntegranteMapper integranteMapper;

    @BeforeEach
    void setUp() {
        integranteMapper = new IntegranteMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getIntegranteSample1();
        var actual = integranteMapper.toEntity(integranteMapper.toDto(expected));
        assertIntegranteAllPropertiesEquals(expected, actual);
    }
}
