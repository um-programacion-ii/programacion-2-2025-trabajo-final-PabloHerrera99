package com.evento.backend.domain;

import com.evento.backend.domain.enumeration.EstadoSincronizacion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Entidad 6: Venta
 * Registro de intentos de venta (exitosos y fallidos)
 * Mantiene estado de sincronización con servidor de cátedra
 */
@Entity
@Table(name = "venta")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Venta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "id_venta_catedra")
    private Long idVentaCatedra;

    @NotNull
    @Column(name = "fecha_venta", nullable = false)
    private Instant fechaVenta;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "precio_total", precision = 21, scale = 2, nullable = false)
    private BigDecimal precioTotal;

    @NotNull
    @Column(name = "exitosa", nullable = false)
    private Boolean exitosa;

    @Size(max = 500)
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_sincronizacion", nullable = false)
    private EstadoSincronizacion estadoSincronizacion;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "eventoTipo" }, allowSetters = true)
    private Evento evento;

    @ManyToOne(optional = false)
    @NotNull
    private User usuario;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Venta id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdVentaCatedra() {
        return this.idVentaCatedra;
    }

    public Venta idVentaCatedra(Long idVentaCatedra) {
        this.setIdVentaCatedra(idVentaCatedra);
        return this;
    }

    public void setIdVentaCatedra(Long idVentaCatedra) {
        this.idVentaCatedra = idVentaCatedra;
    }

    public Instant getFechaVenta() {
        return this.fechaVenta;
    }

    public Venta fechaVenta(Instant fechaVenta) {
        this.setFechaVenta(fechaVenta);
        return this;
    }

    public void setFechaVenta(Instant fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getPrecioTotal() {
        return this.precioTotal;
    }

    public Venta precioTotal(BigDecimal precioTotal) {
        this.setPrecioTotal(precioTotal);
        return this;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }

    public Boolean getExitosa() {
        return this.exitosa;
    }

    public Venta exitosa(Boolean exitosa) {
        this.setExitosa(exitosa);
        return this;
    }

    public void setExitosa(Boolean exitosa) {
        this.exitosa = exitosa;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public Venta descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoSincronizacion getEstadoSincronizacion() {
        return this.estadoSincronizacion;
    }

    public Venta estadoSincronizacion(EstadoSincronizacion estadoSincronizacion) {
        this.setEstadoSincronizacion(estadoSincronizacion);
        return this;
    }

    public void setEstadoSincronizacion(EstadoSincronizacion estadoSincronizacion) {
        this.estadoSincronizacion = estadoSincronizacion;
    }

    public Evento getEvento() {
        return this.evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Venta evento(Evento evento) {
        this.setEvento(evento);
        return this;
    }

    public User getUsuario() {
        return this.usuario;
    }

    public void setUsuario(User user) {
        this.usuario = user;
    }

    public Venta usuario(User user) {
        this.setUsuario(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Venta)) {
            return false;
        }
        return getId() != null && getId().equals(((Venta) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Venta{" +
            "id=" + getId() +
            ", idVentaCatedra=" + getIdVentaCatedra() +
            ", fechaVenta='" + getFechaVenta() + "'" +
            ", precioTotal=" + getPrecioTotal() +
            ", exitosa='" + getExitosa() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", estadoSincronizacion='" + getEstadoSincronizacion() + "'" +
            "}";
    }
}
