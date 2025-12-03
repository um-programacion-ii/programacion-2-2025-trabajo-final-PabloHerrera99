package com.evento.backend.domain;

import static com.evento.backend.domain.EventoTestSamples.*;
import static com.evento.backend.domain.EventoTipoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.evento.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Evento.class);
        Evento evento1 = getEventoSample1();
        Evento evento2 = new Evento();
        assertThat(evento1).isNotEqualTo(evento2);

        evento2.setId(evento1.getId());
        assertThat(evento1).isEqualTo(evento2);

        evento2 = getEventoSample2();
        assertThat(evento1).isNotEqualTo(evento2);
    }

    @Test
    void eventoTipoTest() {
        Evento evento = getEventoRandomSampleGenerator();
        EventoTipo eventoTipoBack = getEventoTipoRandomSampleGenerator();

        evento.setEventoTipo(eventoTipoBack);
        assertThat(evento.getEventoTipo()).isEqualTo(eventoTipoBack);

        evento.eventoTipo(null);
        assertThat(evento.getEventoTipo()).isNull();
    }
}
