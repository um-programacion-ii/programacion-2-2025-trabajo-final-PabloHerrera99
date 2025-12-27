package com.evento.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.evento.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AsientoSeleccionadoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AsientoSeleccionadoDTO.class);
        AsientoSeleccionadoDTO asientoSeleccionadoDTO1 = new AsientoSeleccionadoDTO();
        asientoSeleccionadoDTO1.setId(1L);
        AsientoSeleccionadoDTO asientoSeleccionadoDTO2 = new AsientoSeleccionadoDTO();
        assertThat(asientoSeleccionadoDTO1).isNotEqualTo(asientoSeleccionadoDTO2);
        asientoSeleccionadoDTO2.setId(asientoSeleccionadoDTO1.getId());
        assertThat(asientoSeleccionadoDTO1).isEqualTo(asientoSeleccionadoDTO2);
        asientoSeleccionadoDTO2.setId(2L);
        assertThat(asientoSeleccionadoDTO1).isNotEqualTo(asientoSeleccionadoDTO2);
        asientoSeleccionadoDTO1.setId(null);
        assertThat(asientoSeleccionadoDTO1).isNotEqualTo(asientoSeleccionadoDTO2);
    }
}
