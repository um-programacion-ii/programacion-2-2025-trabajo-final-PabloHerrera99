package com.evento.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.evento.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SesionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SesionDTO.class);
        SesionDTO sesionDTO1 = new SesionDTO();
        sesionDTO1.setId(1L);
        SesionDTO sesionDTO2 = new SesionDTO();
        assertThat(sesionDTO1).isNotEqualTo(sesionDTO2);
        sesionDTO2.setId(sesionDTO1.getId());
        assertThat(sesionDTO1).isEqualTo(sesionDTO2);
        sesionDTO2.setId(2L);
        assertThat(sesionDTO1).isNotEqualTo(sesionDTO2);
        sesionDTO1.setId(null);
        assertThat(sesionDTO1).isNotEqualTo(sesionDTO2);
    }
}
