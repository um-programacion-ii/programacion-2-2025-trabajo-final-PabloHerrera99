package com.evento.backend.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.evento.backend.domain.Evento} entity. This class is used
 * in {@link com.evento.backend.web.rest.EventoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /eventos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventoCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter idCatedra;

    private StringFilter titulo;

    private StringFilter resumen;

    private InstantFilter fecha;

    private StringFilter direccion;

    private StringFilter imagen;

    private IntegerFilter filaAsientos;

    private IntegerFilter columnaAsientos;

    private BigDecimalFilter precioEntrada;

    private BooleanFilter activo;

    private InstantFilter fechaSincronizacion;

    private LongFilter eventoTipoId;

    private Boolean distinct;

    public EventoCriteria() {}

    public EventoCriteria(EventoCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.idCatedra = other.optionalIdCatedra().map(LongFilter::copy).orElse(null);
        this.titulo = other.optionalTitulo().map(StringFilter::copy).orElse(null);
        this.resumen = other.optionalResumen().map(StringFilter::copy).orElse(null);
        this.fecha = other.optionalFecha().map(InstantFilter::copy).orElse(null);
        this.direccion = other.optionalDireccion().map(StringFilter::copy).orElse(null);
        this.imagen = other.optionalImagen().map(StringFilter::copy).orElse(null);
        this.filaAsientos = other.optionalFilaAsientos().map(IntegerFilter::copy).orElse(null);
        this.columnaAsientos = other.optionalColumnaAsientos().map(IntegerFilter::copy).orElse(null);
        this.precioEntrada = other.optionalPrecioEntrada().map(BigDecimalFilter::copy).orElse(null);
        this.activo = other.optionalActivo().map(BooleanFilter::copy).orElse(null);
        this.fechaSincronizacion = other.optionalFechaSincronizacion().map(InstantFilter::copy).orElse(null);
        this.eventoTipoId = other.optionalEventoTipoId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public EventoCriteria copy() {
        return new EventoCriteria(this);
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

    public LongFilter getIdCatedra() {
        return idCatedra;
    }

    public Optional<LongFilter> optionalIdCatedra() {
        return Optional.ofNullable(idCatedra);
    }

    public LongFilter idCatedra() {
        if (idCatedra == null) {
            setIdCatedra(new LongFilter());
        }
        return idCatedra;
    }

    public void setIdCatedra(LongFilter idCatedra) {
        this.idCatedra = idCatedra;
    }

    public StringFilter getTitulo() {
        return titulo;
    }

    public Optional<StringFilter> optionalTitulo() {
        return Optional.ofNullable(titulo);
    }

    public StringFilter titulo() {
        if (titulo == null) {
            setTitulo(new StringFilter());
        }
        return titulo;
    }

    public void setTitulo(StringFilter titulo) {
        this.titulo = titulo;
    }

    public StringFilter getResumen() {
        return resumen;
    }

    public Optional<StringFilter> optionalResumen() {
        return Optional.ofNullable(resumen);
    }

    public StringFilter resumen() {
        if (resumen == null) {
            setResumen(new StringFilter());
        }
        return resumen;
    }

    public void setResumen(StringFilter resumen) {
        this.resumen = resumen;
    }

    public InstantFilter getFecha() {
        return fecha;
    }

    public Optional<InstantFilter> optionalFecha() {
        return Optional.ofNullable(fecha);
    }

    public InstantFilter fecha() {
        if (fecha == null) {
            setFecha(new InstantFilter());
        }
        return fecha;
    }

    public void setFecha(InstantFilter fecha) {
        this.fecha = fecha;
    }

    public StringFilter getDireccion() {
        return direccion;
    }

    public Optional<StringFilter> optionalDireccion() {
        return Optional.ofNullable(direccion);
    }

    public StringFilter direccion() {
        if (direccion == null) {
            setDireccion(new StringFilter());
        }
        return direccion;
    }

    public void setDireccion(StringFilter direccion) {
        this.direccion = direccion;
    }

    public StringFilter getImagen() {
        return imagen;
    }

    public Optional<StringFilter> optionalImagen() {
        return Optional.ofNullable(imagen);
    }

    public StringFilter imagen() {
        if (imagen == null) {
            setImagen(new StringFilter());
        }
        return imagen;
    }

    public void setImagen(StringFilter imagen) {
        this.imagen = imagen;
    }

    public IntegerFilter getFilaAsientos() {
        return filaAsientos;
    }

    public Optional<IntegerFilter> optionalFilaAsientos() {
        return Optional.ofNullable(filaAsientos);
    }

    public IntegerFilter filaAsientos() {
        if (filaAsientos == null) {
            setFilaAsientos(new IntegerFilter());
        }
        return filaAsientos;
    }

    public void setFilaAsientos(IntegerFilter filaAsientos) {
        this.filaAsientos = filaAsientos;
    }

    public IntegerFilter getColumnaAsientos() {
        return columnaAsientos;
    }

    public Optional<IntegerFilter> optionalColumnaAsientos() {
        return Optional.ofNullable(columnaAsientos);
    }

    public IntegerFilter columnaAsientos() {
        if (columnaAsientos == null) {
            setColumnaAsientos(new IntegerFilter());
        }
        return columnaAsientos;
    }

    public void setColumnaAsientos(IntegerFilter columnaAsientos) {
        this.columnaAsientos = columnaAsientos;
    }

    public BigDecimalFilter getPrecioEntrada() {
        return precioEntrada;
    }

    public Optional<BigDecimalFilter> optionalPrecioEntrada() {
        return Optional.ofNullable(precioEntrada);
    }

    public BigDecimalFilter precioEntrada() {
        if (precioEntrada == null) {
            setPrecioEntrada(new BigDecimalFilter());
        }
        return precioEntrada;
    }

    public void setPrecioEntrada(BigDecimalFilter precioEntrada) {
        this.precioEntrada = precioEntrada;
    }

    public BooleanFilter getActivo() {
        return activo;
    }

    public Optional<BooleanFilter> optionalActivo() {
        return Optional.ofNullable(activo);
    }

    public BooleanFilter activo() {
        if (activo == null) {
            setActivo(new BooleanFilter());
        }
        return activo;
    }

    public void setActivo(BooleanFilter activo) {
        this.activo = activo;
    }

    public InstantFilter getFechaSincronizacion() {
        return fechaSincronizacion;
    }

    public Optional<InstantFilter> optionalFechaSincronizacion() {
        return Optional.ofNullable(fechaSincronizacion);
    }

    public InstantFilter fechaSincronizacion() {
        if (fechaSincronizacion == null) {
            setFechaSincronizacion(new InstantFilter());
        }
        return fechaSincronizacion;
    }

    public void setFechaSincronizacion(InstantFilter fechaSincronizacion) {
        this.fechaSincronizacion = fechaSincronizacion;
    }

    public LongFilter getEventoTipoId() {
        return eventoTipoId;
    }

    public Optional<LongFilter> optionalEventoTipoId() {
        return Optional.ofNullable(eventoTipoId);
    }

    public LongFilter eventoTipoId() {
        if (eventoTipoId == null) {
            setEventoTipoId(new LongFilter());
        }
        return eventoTipoId;
    }

    public void setEventoTipoId(LongFilter eventoTipoId) {
        this.eventoTipoId = eventoTipoId;
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
        final EventoCriteria that = (EventoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(idCatedra, that.idCatedra) &&
            Objects.equals(titulo, that.titulo) &&
            Objects.equals(resumen, that.resumen) &&
            Objects.equals(fecha, that.fecha) &&
            Objects.equals(direccion, that.direccion) &&
            Objects.equals(imagen, that.imagen) &&
            Objects.equals(filaAsientos, that.filaAsientos) &&
            Objects.equals(columnaAsientos, that.columnaAsientos) &&
            Objects.equals(precioEntrada, that.precioEntrada) &&
            Objects.equals(activo, that.activo) &&
            Objects.equals(fechaSincronizacion, that.fechaSincronizacion) &&
            Objects.equals(eventoTipoId, that.eventoTipoId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            idCatedra,
            titulo,
            resumen,
            fecha,
            direccion,
            imagen,
            filaAsientos,
            columnaAsientos,
            precioEntrada,
            activo,
            fechaSincronizacion,
            eventoTipoId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventoCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalIdCatedra().map(f -> "idCatedra=" + f + ", ").orElse("") +
            optionalTitulo().map(f -> "titulo=" + f + ", ").orElse("") +
            optionalResumen().map(f -> "resumen=" + f + ", ").orElse("") +
            optionalFecha().map(f -> "fecha=" + f + ", ").orElse("") +
            optionalDireccion().map(f -> "direccion=" + f + ", ").orElse("") +
            optionalImagen().map(f -> "imagen=" + f + ", ").orElse("") +
            optionalFilaAsientos().map(f -> "filaAsientos=" + f + ", ").orElse("") +
            optionalColumnaAsientos().map(f -> "columnaAsientos=" + f + ", ").orElse("") +
            optionalPrecioEntrada().map(f -> "precioEntrada=" + f + ", ").orElse("") +
            optionalActivo().map(f -> "activo=" + f + ", ").orElse("") +
            optionalFechaSincronizacion().map(f -> "fechaSincronizacion=" + f + ", ").orElse("") +
            optionalEventoTipoId().map(f -> "eventoTipoId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
