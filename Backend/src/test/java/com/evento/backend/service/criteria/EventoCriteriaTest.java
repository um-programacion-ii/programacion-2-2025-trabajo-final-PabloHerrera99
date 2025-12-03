package com.evento.backend.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class EventoCriteriaTest {

    @Test
    void newEventoCriteriaHasAllFiltersNullTest() {
        var eventoCriteria = new EventoCriteria();
        assertThat(eventoCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void eventoCriteriaFluentMethodsCreatesFiltersTest() {
        var eventoCriteria = new EventoCriteria();

        setAllFilters(eventoCriteria);

        assertThat(eventoCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void eventoCriteriaCopyCreatesNullFilterTest() {
        var eventoCriteria = new EventoCriteria();
        var copy = eventoCriteria.copy();

        assertThat(eventoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(eventoCriteria)
        );
    }

    @Test
    void eventoCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var eventoCriteria = new EventoCriteria();
        setAllFilters(eventoCriteria);

        var copy = eventoCriteria.copy();

        assertThat(eventoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(eventoCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var eventoCriteria = new EventoCriteria();

        assertThat(eventoCriteria).hasToString("EventoCriteria{}");
    }

    private static void setAllFilters(EventoCriteria eventoCriteria) {
        eventoCriteria.id();
        eventoCriteria.idCatedra();
        eventoCriteria.titulo();
        eventoCriteria.resumen();
        eventoCriteria.fecha();
        eventoCriteria.direccion();
        eventoCriteria.imagen();
        eventoCriteria.filaAsientos();
        eventoCriteria.columnaAsientos();
        eventoCriteria.precioEntrada();
        eventoCriteria.activo();
        eventoCriteria.fechaSincronizacion();
        eventoCriteria.eventoTipoId();
        eventoCriteria.distinct();
    }

    private static Condition<EventoCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getIdCatedra()) &&
                condition.apply(criteria.getTitulo()) &&
                condition.apply(criteria.getResumen()) &&
                condition.apply(criteria.getFecha()) &&
                condition.apply(criteria.getDireccion()) &&
                condition.apply(criteria.getImagen()) &&
                condition.apply(criteria.getFilaAsientos()) &&
                condition.apply(criteria.getColumnaAsientos()) &&
                condition.apply(criteria.getPrecioEntrada()) &&
                condition.apply(criteria.getActivo()) &&
                condition.apply(criteria.getFechaSincronizacion()) &&
                condition.apply(criteria.getEventoTipoId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<EventoCriteria> copyFiltersAre(EventoCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getIdCatedra(), copy.getIdCatedra()) &&
                condition.apply(criteria.getTitulo(), copy.getTitulo()) &&
                condition.apply(criteria.getResumen(), copy.getResumen()) &&
                condition.apply(criteria.getFecha(), copy.getFecha()) &&
                condition.apply(criteria.getDireccion(), copy.getDireccion()) &&
                condition.apply(criteria.getImagen(), copy.getImagen()) &&
                condition.apply(criteria.getFilaAsientos(), copy.getFilaAsientos()) &&
                condition.apply(criteria.getColumnaAsientos(), copy.getColumnaAsientos()) &&
                condition.apply(criteria.getPrecioEntrada(), copy.getPrecioEntrada()) &&
                condition.apply(criteria.getActivo(), copy.getActivo()) &&
                condition.apply(criteria.getFechaSincronizacion(), copy.getFechaSincronizacion()) &&
                condition.apply(criteria.getEventoTipoId(), copy.getEventoTipoId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
