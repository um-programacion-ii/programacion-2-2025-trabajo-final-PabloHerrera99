package com.evento.backend.service.dto;

import com.evento.backend.domain.enumeration.EstadoSesion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.evento.backend.domain.Sesion} entity.
 */
@Schema(
    description = "Entidad 4: Sesion\nRepresenta la sesión de un usuario durante el proceso de compra\nPermite recuperar el estado si el usuario cierra y vuelve a abrir la app"
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SesionDTO implements Serializable {

    private Long id;

    @NotNull
    private EstadoSesion estado;

    @NotNull
    private Instant fechaInicio;

    @NotNull
    private Instant ultimaActividad;

    @NotNull
    private Instant expiracion;

    @NotNull
    private Boolean activa;

    @NotNull
    private UserDTO usuario;

    @NotNull
    private EventoDTO evento;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EstadoSesion getEstado() {
        return estado;
    }

    public void setEstado(EstadoSesion estado) {
        this.estado = estado;
    }

    public Instant getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Instant fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Instant getUltimaActividad() {
        return ultimaActividad;
    }

    public void setUltimaActividad(Instant ultimaActividad) {
        this.ultimaActividad = ultimaActividad;
    }

    public Instant getExpiracion() {
        return expiracion;
    }

    public void setExpiracion(Instant expiracion) {
        this.expiracion = expiracion;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public UserDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UserDTO usuario) {
        this.usuario = usuario;
    }

    public EventoDTO getEvento() {
        return evento;
    }

    public void setEvento(EventoDTO evento) {
        this.evento = evento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SesionDTO)) {
            return false;
        }

        SesionDTO sesionDTO = (SesionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, sesionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SesionDTO{" +
            "id=" + getId() +
            ", estado='" + getEstado() + "'" +
            ", fechaInicio='" + getFechaInicio() + "'" +
            ", ultimaActividad='" + getUltimaActividad() + "'" +
            ", expiracion='" + getExpiracion() + "'" +
            ", activa='" + getActiva() + "'" +
            ", usuario=" + getUsuario() +
            ", evento=" + getEvento() +
            "}";
    }
}
