package com.evento.backend.service.criteria;

import com.evento.backend.domain.enumeration.EstadoSincronizacion;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.evento.backend.domain.Venta} entity. This class is used
 * in {@link com.evento.backend.web.rest.VentaResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ventas?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VentaCriteria implements Serializable, Criteria {

    /**
     * Class for filtering EstadoSincronizacion
     */
    public static class EstadoSincronizacionFilter extends Filter<EstadoSincronizacion> {

        public EstadoSincronizacionFilter() {}

        public EstadoSincronizacionFilter(EstadoSincronizacionFilter filter) {
            super(filter);
        }

        @Override
        public EstadoSincronizacionFilter copy() {
            return new EstadoSincronizacionFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter idVentaCatedra;

    private InstantFilter fechaVenta;

    private BigDecimalFilter precioTotal;

    private BooleanFilter exitosa;

    private StringFilter descripcion;

    private EstadoSincronizacionFilter estadoSincronizacion;

    private LongFilter eventoId;

    private LongFilter usuarioId;

    private Boolean distinct;

    public VentaCriteria() {}

    public VentaCriteria(VentaCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.idVentaCatedra = other.optionalIdVentaCatedra().map(LongFilter::copy).orElse(null);
        this.fechaVenta = other.optionalFechaVenta().map(InstantFilter::copy).orElse(null);
        this.precioTotal = other.optionalPrecioTotal().map(BigDecimalFilter::copy).orElse(null);
        this.exitosa = other.optionalExitosa().map(BooleanFilter::copy).orElse(null);
        this.descripcion = other.optionalDescripcion().map(StringFilter::copy).orElse(null);
        this.estadoSincronizacion = other.optionalEstadoSincronizacion().map(EstadoSincronizacionFilter::copy).orElse(null);
        this.eventoId = other.optionalEventoId().map(LongFilter::copy).orElse(null);
        this.usuarioId = other.optionalUsuarioId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public VentaCriteria copy() {
        return new VentaCriteria(this);
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

    public LongFilter getIdVentaCatedra() {
        return idVentaCatedra;
    }

    public Optional<LongFilter> optionalIdVentaCatedra() {
        return Optional.ofNullable(idVentaCatedra);
    }

    public LongFilter idVentaCatedra() {
        if (idVentaCatedra == null) {
            setIdVentaCatedra(new LongFilter());
        }
        return idVentaCatedra;
    }

    public void setIdVentaCatedra(LongFilter idVentaCatedra) {
        this.idVentaCatedra = idVentaCatedra;
    }

    public InstantFilter getFechaVenta() {
        return fechaVenta;
    }

    public Optional<InstantFilter> optionalFechaVenta() {
        return Optional.ofNullable(fechaVenta);
    }

    public InstantFilter fechaVenta() {
        if (fechaVenta == null) {
            setFechaVenta(new InstantFilter());
        }
        return fechaVenta;
    }

    public void setFechaVenta(InstantFilter fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimalFilter getPrecioTotal() {
        return precioTotal;
    }

    public Optional<BigDecimalFilter> optionalPrecioTotal() {
        return Optional.ofNullable(precioTotal);
    }

    public BigDecimalFilter precioTotal() {
        if (precioTotal == null) {
            setPrecioTotal(new BigDecimalFilter());
        }
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimalFilter precioTotal) {
        this.precioTotal = precioTotal;
    }

    public BooleanFilter getExitosa() {
        return exitosa;
    }

    public Optional<BooleanFilter> optionalExitosa() {
        return Optional.ofNullable(exitosa);
    }

    public BooleanFilter exitosa() {
        if (exitosa == null) {
            setExitosa(new BooleanFilter());
        }
        return exitosa;
    }

    public void setExitosa(BooleanFilter exitosa) {
        this.exitosa = exitosa;
    }

    public StringFilter getDescripcion() {
        return descripcion;
    }

    public Optional<StringFilter> optionalDescripcion() {
        return Optional.ofNullable(descripcion);
    }

    public StringFilter descripcion() {
        if (descripcion == null) {
            setDescripcion(new StringFilter());
        }
        return descripcion;
    }

    public void setDescripcion(StringFilter descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoSincronizacionFilter getEstadoSincronizacion() {
        return estadoSincronizacion;
    }

    public Optional<EstadoSincronizacionFilter> optionalEstadoSincronizacion() {
        return Optional.ofNullable(estadoSincronizacion);
    }

    public EstadoSincronizacionFilter estadoSincronizacion() {
        if (estadoSincronizacion == null) {
            setEstadoSincronizacion(new EstadoSincronizacionFilter());
        }
        return estadoSincronizacion;
    }

    public void setEstadoSincronizacion(EstadoSincronizacionFilter estadoSincronizacion) {
        this.estadoSincronizacion = estadoSincronizacion;
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
        final VentaCriteria that = (VentaCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(idVentaCatedra, that.idVentaCatedra) &&
            Objects.equals(fechaVenta, that.fechaVenta) &&
            Objects.equals(precioTotal, that.precioTotal) &&
            Objects.equals(exitosa, that.exitosa) &&
            Objects.equals(descripcion, that.descripcion) &&
            Objects.equals(estadoSincronizacion, that.estadoSincronizacion) &&
            Objects.equals(eventoId, that.eventoId) &&
            Objects.equals(usuarioId, that.usuarioId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            idVentaCatedra,
            fechaVenta,
            precioTotal,
            exitosa,
            descripcion,
            estadoSincronizacion,
            eventoId,
            usuarioId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VentaCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalIdVentaCatedra().map(f -> "idVentaCatedra=" + f + ", ").orElse("") +
            optionalFechaVenta().map(f -> "fechaVenta=" + f + ", ").orElse("") +
            optionalPrecioTotal().map(f -> "precioTotal=" + f + ", ").orElse("") +
            optionalExitosa().map(f -> "exitosa=" + f + ", ").orElse("") +
            optionalDescripcion().map(f -> "descripcion=" + f + ", ").orElse("") +
            optionalEstadoSincronizacion().map(f -> "estadoSincronizacion=" + f + ", ").orElse("") +
            optionalEventoId().map(f -> "eventoId=" + f + ", ").orElse("") +
            optionalUsuarioId().map(f -> "usuarioId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
