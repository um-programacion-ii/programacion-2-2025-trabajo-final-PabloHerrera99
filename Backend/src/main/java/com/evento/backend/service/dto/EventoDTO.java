package com.evento.backend.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.evento.backend.domain.Evento} entity.
 */
@Schema(
    description = "Entidad 2: Evento\nEntidad principal que almacena información de eventos sincronizada\ndesde el servidor de la cátedra"
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventoDTO implements Serializable {

    private Long id;

    private Long idCatedra;

    @NotNull
    @Size(max = 200)
    private String titulo;

    @Size(max = 500)
    private String resumen;

    @Lob
    private String descripcion;

    @NotNull
    private Instant fecha;

    @Size(max = 300)
    private String direccion;

    @Size(max = 1000)
    private String imagen;

    @Min(value = 1)
    @Max(value = 100)
    private Integer filaAsientos;

    @Min(value = 1)
    @Max(value = 100)
    private Integer columnaAsientos;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal precioEntrada;

    private Boolean activo;

    private Instant fechaSincronizacion;

    @NotNull
    private EventoTipoDTO eventoTipo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCatedra() {
        return idCatedra;
    }

    public void setIdCatedra(Long idCatedra) {
        this.idCatedra = idCatedra;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getFilaAsientos() {
        return filaAsientos;
    }

    public void setFilaAsientos(Integer filaAsientos) {
        this.filaAsientos = filaAsientos;
    }

    public Integer getColumnaAsientos() {
        return columnaAsientos;
    }

    public void setColumnaAsientos(Integer columnaAsientos) {
        this.columnaAsientos = columnaAsientos;
    }

    public BigDecimal getPrecioEntrada() {
        return precioEntrada;
    }

    public void setPrecioEntrada(BigDecimal precioEntrada) {
        this.precioEntrada = precioEntrada;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Instant getFechaSincronizacion() {
        return fechaSincronizacion;
    }

    public void setFechaSincronizacion(Instant fechaSincronizacion) {
        this.fechaSincronizacion = fechaSincronizacion;
    }

    public EventoTipoDTO getEventoTipo() {
        return eventoTipo;
    }

    public void setEventoTipo(EventoTipoDTO eventoTipo) {
        this.eventoTipo = eventoTipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventoDTO)) {
            return false;
        }

        EventoDTO eventoDTO = (EventoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, eventoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventoDTO{" +
            "id=" + getId() +
            ", idCatedra=" + getIdCatedra() +
            ", titulo='" + getTitulo() + "'" +
            ", resumen='" + getResumen() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", fecha='" + getFecha() + "'" +
            ", direccion='" + getDireccion() + "'" +
            ", imagen='" + getImagen() + "'" +
            ", filaAsientos=" + getFilaAsientos() +
            ", columnaAsientos=" + getColumnaAsientos() +
            ", precioEntrada=" + getPrecioEntrada() +
            ", activo='" + getActivo() + "'" +
            ", fechaSincronizacion='" + getFechaSincronizacion() + "'" +
            ", eventoTipo=" + getEventoTipo() +
            "}";
    }
}
