package com.evento.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Entidad 7: AsientoVendido
 * Detalle de asientos incluidos en una venta completada
 */
@Entity
@Table(name = "asiento_vendido")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AsientoVendido implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 1)
    @Column(name = "fila", nullable = false)
    private Integer fila;

    @NotNull
    @Min(value = 1)
    @Column(name = "columna", nullable = false)
    private Integer columna;

    @NotNull
    @Size(max = 200)
    @Column(name = "nombre_persona", length = 200, nullable = false)
    private String nombrePersona;

    @Size(max = 50)
    @Column(name = "estado", length = 50)
    private String estado;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "evento", "usuario" }, allowSetters = true)
    private Venta venta;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AsientoVendido id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFila() {
        return this.fila;
    }

    public AsientoVendido fila(Integer fila) {
        this.setFila(fila);
        return this;
    }

    public void setFila(Integer fila) {
        this.fila = fila;
    }

    public Integer getColumna() {
        return this.columna;
    }

    public AsientoVendido columna(Integer columna) {
        this.setColumna(columna);
        return this;
    }

    public void setColumna(Integer columna) {
        this.columna = columna;
    }

    public String getNombrePersona() {
        return this.nombrePersona;
    }

    public AsientoVendido nombrePersona(String nombrePersona) {
        this.setNombrePersona(nombrePersona);
        return this;
    }

    public void setNombrePersona(String nombrePersona) {
        this.nombrePersona = nombrePersona;
    }

    public String getEstado() {
        return this.estado;
    }

    public AsientoVendido estado(String estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Venta getVenta() {
        return this.venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public AsientoVendido venta(Venta venta) {
        this.setVenta(venta);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AsientoVendido)) {
            return false;
        }
        return getId() != null && getId().equals(((AsientoVendido) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AsientoVendido{" +
            "id=" + getId() +
            ", fila=" + getFila() +
            ", columna=" + getColumna() +
            ", nombrePersona='" + getNombrePersona() + "'" +
            ", estado='" + getEstado() + "'" +
            "}";
    }
}
