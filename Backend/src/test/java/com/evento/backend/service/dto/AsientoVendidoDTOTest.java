package com.evento.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.evento.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AsientoVendidoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AsientoVendidoDTO.class);
        AsientoVendidoDTO asientoVendidoDTO1 = new AsientoVendidoDTO();
        asientoVendidoDTO1.setId(1L);
        AsientoVendidoDTO asientoVendidoDTO2 = new AsientoVendidoDTO();
        assertThat(asientoVendidoDTO1).isNotEqualTo(asientoVendidoDTO2);
        asientoVendidoDTO2.setId(asientoVendidoDTO1.getId());
        assertThat(asientoVendidoDTO1).isEqualTo(asientoVendidoDTO2);
        asientoVendidoDTO2.setId(2L);
        assertThat(asientoVendidoDTO1).isNotEqualTo(asientoVendidoDTO2);
        asientoVendidoDTO1.setId(null);
        assertThat(asientoVendidoDTO1).isNotEqualTo(asientoVendidoDTO2);
    }
}
