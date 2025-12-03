package com.evento.backend.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SesionCriteriaTest {

    @Test
    void newSesionCriteriaHasAllFiltersNullTest() {
        var sesionCriteria = new SesionCriteria();
        assertThat(sesionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void sesionCriteriaFluentMethodsCreatesFiltersTest() {
        var sesionCriteria = new SesionCriteria();

        setAllFilters(sesionCriteria);

        assertThat(sesionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void sesionCriteriaCopyCreatesNullFilterTest() {
        var sesionCriteria = new SesionCriteria();
        var copy = sesionCriteria.copy();

        assertThat(sesionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(sesionCriteria)
        );
    }

    @Test
    void sesionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var sesionCriteria = new SesionCriteria();
        setAllFilters(sesionCriteria);

        var copy = sesionCriteria.copy();

        assertThat(sesionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(sesionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var sesionCriteria = new SesionCriteria();

        assertThat(sesionCriteria).hasToString("SesionCriteria{}");
    }

    private static void setAllFilters(SesionCriteria sesionCriteria) {
        sesionCriteria.id();
        sesionCriteria.estado();
        sesionCriteria.fechaInicio();
        sesionCriteria.ultimaActividad();
        sesionCriteria.expiracion();
        sesionCriteria.activa();
        sesionCriteria.usuarioId();
        sesionCriteria.eventoId();
        sesionCriteria.distinct();
    }

    private static Condition<SesionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getEstado()) &&
                condition.apply(criteria.getFechaInicio()) &&
                condition.apply(criteria.getUltimaActividad()) &&
                condition.apply(criteria.getExpiracion()) &&
                condition.apply(criteria.getActiva()) &&
                condition.apply(criteria.getUsuarioId()) &&
                condition.apply(criteria.getEventoId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SesionCriteria> copyFiltersAre(SesionCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getEstado(), copy.getEstado()) &&
                condition.apply(criteria.getFechaInicio(), copy.getFechaInicio()) &&
                condition.apply(criteria.getUltimaActividad(), copy.getUltimaActividad()) &&
                condition.apply(criteria.getExpiracion(), copy.getExpiracion()) &&
                condition.apply(criteria.getActiva(), copy.getActiva()) &&
                condition.apply(criteria.getUsuarioId(), copy.getUsuarioId()) &&
                condition.apply(criteria.getEventoId(), copy.getEventoId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
