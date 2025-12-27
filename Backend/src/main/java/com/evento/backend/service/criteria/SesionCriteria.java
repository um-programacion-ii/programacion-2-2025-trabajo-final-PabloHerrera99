package com.evento.backend.service.criteria;

import com.evento.backend.domain.enumeration.EstadoSesion;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.evento.backend.domain.Sesion} entity. This class is used
 * in {@link com.evento.backend.web.rest.SesionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sesions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SesionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering EstadoSesion
     */
    public static class EstadoSesionFilter extends Filter<EstadoSesion> {

        public EstadoSesionFilter() {}

        public EstadoSesionFilter(EstadoSesionFilter filter) {
            super(filter);
        }

        @Override
        public EstadoSesionFilter copy() {
            return new EstadoSesionFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private EstadoSesionFilter estado;

    private InstantFilter fechaInicio;

    private InstantFilter ultimaActividad;

    private InstantFilter expiracion;

    private BooleanFilter activa;

    private LongFilter usuarioId;

    private LongFilter eventoId;

    private Boolean distinct;

    public SesionCriteria() {}

    public SesionCriteria(SesionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.estado = other.optionalEstado().map(EstadoSesionFilter::copy).orElse(null);
        this.fechaInicio = other.optionalFechaInicio().map(InstantFilter::copy).orElse(null);
        this.ultimaActividad = other.optionalUltimaActividad().map(InstantFilter::copy).orElse(null);
        this.expiracion = other.optionalExpiracion().map(InstantFilter::copy).orElse(null);
        this.activa = other.optionalActiva().map(BooleanFilter::copy).orElse(null);
        this.usuarioId = other.optionalUsuarioId().map(LongFilter::copy).orElse(null);
        this.eventoId = other.optionalEventoId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public SesionCriteria copy() {
        return new SesionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public EstadoSesionFilter getEstado() {
        return estado;
    }

    public Optional<EstadoSesionFilter> optionalEstado() {
        return Optional.ofNullable(estado);
    }

    public EstadoSesionFilter estado() {
        if (estado == null) {
            setEstado(new EstadoSesionFilter());
        }
        return estado;
    }

    public void setEstado(EstadoSesionFilter estado) {
        this.estado = estado;
    }

    public InstantFilter getFechaInicio() {
        return fechaInicio;
    }

    public Optional<InstantFilter> optionalFechaInicio() {
        return Optional.ofNullable(fechaInicio);
    }

    public InstantFilter fechaInicio() {
        if (fechaInicio == null) {
            setFechaInicio(new InstantFilter());
        }
        return fechaInicio;
    }

    public void setFechaInicio(InstantFilter fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public InstantFilter getUltimaActividad() {
        return ultimaActividad;
    }

    public Optional<InstantFilter> optionalUltimaActividad() {
        return Optional.ofNullable(ultimaActividad);
    }

    public InstantFilter ultimaActividad() {
        if (ultimaActividad == null) {
            setUltimaActividad(new InstantFilter());
        }
        return ultimaActividad;
    }

    public void setUltimaActividad(InstantFilter ultimaActividad) {
        this.ultimaActividad = ultimaActividad;
    }

    public InstantFilter getExpiracion() {
        return expiracion;
    }

    public Optional<InstantFilter> optionalExpiracion() {
        return Optional.ofNullable(expiracion);
    }

    public InstantFilter expiracion() {
        if (expiracion == null) {
            setExpiracion(new InstantFilter());
        }
        return expiracion;
    }

    public void setExpiracion(InstantFilter expiracion) {
        this.expiracion = expiracion;
    }

    public BooleanFilter getActiva() {
        return activa;
    }

    public Optional<BooleanFilter> optionalActiva() {
        return Optional.ofNullable(activa);
    }

    public BooleanFilter activa() {
        if (activa == null) {
            setActiva(new BooleanFilter());
        }
        return activa;
    }

    public void setActiva(BooleanFilter activa) {
        this.activa = activa;
    }

    public LongFilter getUsuarioId() {
        return usuarioId;
    }

    public Optional<LongFilter> optionalUsuarioId() {
        return Optional.ofNullable(usuarioId);
    }

    public LongFilter usuarioId() {
        if (usuarioId == null) {
            setUsuarioId(new LongFilter());
        }
        return usuarioId;
    }

    public void setUsuarioId(LongFilter usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LongFilter getEventoId() {
        return eventoId;
    }

    public Optional<LongFilter> optionalEventoId() {
        return Optional.ofNullable(eventoId);
    }

    public LongFilter eventoId() {
        if (eventoId == null) {
            setEventoId(new LongFilter());
        }
        return eventoId;
    }

    public void setEventoId(LongFilter eventoId) {
        this.eventoId = eventoId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SesionCriteria that = (SesionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(estado, that.estado) &&
            Objects.equals(fechaInicio, that.fechaInicio) &&
            Objects.equals(ultimaActividad, that.ultimaActividad) &&
            Objects.equals(expiracion, that.expiracion) &&
            Objects.equals(activa, that.activa) &&
            Objects.equals(usuarioId, that.usuarioId) &&
            Objects.equals(eventoId, that.eventoId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, estado, fechaInicio, ultimaActividad, expiracion, activa, usuarioId, eventoId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SesionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalEstado().map(f -> "estado=" + f + ", ").orElse("") +
            optionalFechaInicio().map(f -> "fechaInicio=" + f + ", ").orElse("") +
            optionalUltimaActividad().map(f -> "ultimaActividad=" + f + ", ").orElse("") +
            optionalExpiracion().map(f -> "expiracion=" + f + ", ").orElse("") +
            optionalActiva().map(f -> "activa=" + f + ", ").orElse("") +
            optionalUsuarioId().map(f -> "usuarioId=" + f + ", ").orElse("") +
            optionalEventoId().map(f -> "eventoId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
