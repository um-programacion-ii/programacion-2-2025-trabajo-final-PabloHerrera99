package com.evento.backend.domain;

import static com.evento.backend.domain.AsientoSeleccionadoTestSamples.*;
import static com.evento.backend.domain.SesionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.evento.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AsientoSeleccionadoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AsientoSeleccionado.class);
        AsientoSeleccionado asientoSeleccionado1 = getAsientoSeleccionadoSample1();
        AsientoSeleccionado asientoSeleccionado2 = new AsientoSeleccionado();
        assertThat(asientoSeleccionado1).isNotEqualTo(asientoSeleccionado2);

        asientoSeleccionado2.setId(asientoSeleccionado1.getId());
        assertThat(asientoSeleccionado1).isEqualTo(asientoSeleccionado2);

        asientoSeleccionado2 = getAsientoSeleccionadoSample2();
        assertThat(asientoSeleccionado1).isNotEqualTo(asientoSeleccionado2);
    }

    @Test
    void sesionTest() {
        AsientoSeleccionado asientoSeleccionado = getAsientoSeleccionadoRandomSampleGenerator();
        Sesion sesionBack = getSesionRandomSampleGenerator();

        asientoSeleccionado.setSesion(sesionBack);
        assertThat(asientoSeleccionado.getSesion()).isEqualTo(sesionBack);

        asientoSeleccionado.sesion(null);
        assertThat(asientoSeleccionado.getSesion()).isNull();
    }
}
