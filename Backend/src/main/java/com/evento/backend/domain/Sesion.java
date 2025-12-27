package com.evento.backend.domain;

import com.evento.backend.domain.enumeration.EstadoSesion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Entidad 4: Sesion
 * Representa la sesión de un usuario durante el proceso de compra
 * Permite recuperar el estado si el usuario cierra y vuelve a abrir la app
 */
@Entity
@Table(name = "sesion")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Sesion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoSesion estado;

    @NotNull
    @Column(name = "fecha_inicio", nullable = false)
    private Instant fechaInicio;

    @NotNull
    @Column(name = "ultima_actividad", nullable = false)
    private Instant ultimaActividad;

    @NotNull
    @Column(name = "expiracion", nullable = false)
    private Instant expiracion;

    @NotNull
    @Column(name = "activa", nullable = false)
    private Boolean activa;

    @ManyToOne(optional = false)
    @NotNull
    private User usuario;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "eventoTipo" }, allowSetters = true)
    private Evento evento;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Sesion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EstadoSesion getEstado() {
        return this.estado;
    }

    public Sesion estado(EstadoSesion estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoSesion estado) {
        this.estado = estado;
    }

    public Instant getFechaInicio() {
        return this.fechaInicio;
    }

    public Sesion fechaInicio(Instant fechaInicio) {
        this.setFechaInicio(fechaInicio);
        return this;
    }

    public void setFechaInicio(Instant fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Instant getUltimaActividad() {
        return this.ultimaActividad;
    }

    public Sesion ultimaActividad(Instant ultimaActividad) {
        this.setUltimaActividad(ultimaActividad);
        return this;
    }

    public void setUltimaActividad(Instant ultimaActividad) {
        this.ultimaActividad = ultimaActividad;
    }

    public Instant getExpiracion() {
        return this.expiracion;
    }

    public Sesion expiracion(Instant expiracion) {
        this.setExpiracion(expiracion);
        return this;
    }

    public void setExpiracion(Instant expiracion) {
        this.expiracion = expiracion;
    }

    public Boolean getActiva() {
        return this.activa;
    }

    public Sesion activa(Boolean activa) {
        this.setActiva(activa);
        return this;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public User getUsuario() {
        return this.usuario;
    }

    public void setUsuario(User user) {
        this.usuario = user;
    }

    public Sesion usuario(User user) {
        this.setUsuario(user);
        return this;
    }

    public Evento getEvento() {
        return this.evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Sesion evento(Evento evento) {
        this.setEvento(evento);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sesion)) {
            return false;
        }
        return getId() != null && getId().equals(((Sesion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Sesion{" +
            "id=" + getId() +
            ", estado='" + getEstado() + "'" +
            ", fechaInicio='" + getFechaInicio() + "'" +
            ", ultimaActividad='" + getUltimaActividad() + "'" +
            ", expiracion='" + getExpiracion() + "'" +
            ", activa='" + getActiva() + "'" +
            "}";
    }
}
