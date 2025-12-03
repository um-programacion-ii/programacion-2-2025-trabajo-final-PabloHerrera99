package com.evento.backend.domain;

import static com.evento.backend.domain.EventoTestSamples.*;
import static com.evento.backend.domain.SesionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.evento.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SesionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sesion.class);
        Sesion sesion1 = getSesionSample1();
        Sesion sesion2 = new Sesion();
        assertThat(sesion1).isNotEqualTo(sesion2);

        sesion2.setId(sesion1.getId());
        assertThat(sesion1).isEqualTo(sesion2);

        sesion2 = getSesionSample2();
        assertThat(sesion1).isNotEqualTo(sesion2);
    }

    @Test
    void eventoTest() {
        Sesion sesion = getSesionRandomSampleGenerator();
        Evento eventoBack = getEventoRandomSampleGenerator();

        sesion.setEvento(eventoBack);
        assertThat(sesion.getEvento()).isEqualTo(eventoBack);

        sesion.evento(null);
        assertThat(sesion.getEvento()).isNull();
    }
}
