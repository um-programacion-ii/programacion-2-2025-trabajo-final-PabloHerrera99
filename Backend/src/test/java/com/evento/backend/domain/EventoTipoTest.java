package com.evento.backend.domain;

import static com.evento.backend.domain.EventoTipoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.evento.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventoTipoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventoTipo.class);
        EventoTipo eventoTipo1 = getEventoTipoSample1();
        EventoTipo eventoTipo2 = new EventoTipo();
        assertThat(eventoTipo1).isNotEqualTo(eventoTipo2);

        eventoTipo2.setId(eventoTipo1.getId());
        assertThat(eventoTipo1).isEqualTo(eventoTipo2);

        eventoTipo2 = getEventoTipoSample2();
        assertThat(eventoTipo1).isNotEqualTo(eventoTipo2);
    }
}
