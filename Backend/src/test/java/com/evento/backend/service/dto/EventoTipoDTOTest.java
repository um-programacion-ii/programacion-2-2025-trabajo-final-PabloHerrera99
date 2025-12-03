package com.evento.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.evento.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventoTipoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventoTipoDTO.class);
        EventoTipoDTO eventoTipoDTO1 = new EventoTipoDTO();
        eventoTipoDTO1.setId(1L);
        EventoTipoDTO eventoTipoDTO2 = new EventoTipoDTO();
        assertThat(eventoTipoDTO1).isNotEqualTo(eventoTipoDTO2);
        eventoTipoDTO2.setId(eventoTipoDTO1.getId());
        assertThat(eventoTipoDTO1).isEqualTo(eventoTipoDTO2);
        eventoTipoDTO2.setId(2L);
        assertThat(eventoTipoDTO1).isNotEqualTo(eventoTipoDTO2);
        eventoTipoDTO1.setId(null);
        assertThat(eventoTipoDTO1).isNotEqualTo(eventoTipoDTO2);
    }
}
