package com.evento.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Entidad 5: AsientoSeleccionado
 * Asientos que el usuario ha seleccionado durante su sesión
 * Máximo 4 asientos por sesión (validado en lógica de negocio)
 */
@Entity
@Table(name = "asiento_seleccionado")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AsientoSeleccionado implements Serializable {

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

    @Size(max = 200)
    @Column(name = "nombre_persona", length = 200)
    private String nombrePersona;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "usuario", "evento" }, allowSetters = true)
    private Sesion sesion;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AsientoSeleccionado id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFila() {
        return this.fila;
    }

    public AsientoSeleccionado fila(Integer fila) {
        this.setFila(fila);
        return this;
    }

    public void setFila(Integer fila) {
        this.fila = fila;
    }

    public Integer getColumna() {
        return this.columna;
    }

    public AsientoSeleccionado columna(Integer columna) {
        this.setColumna(columna);
        return this;
    }

    public void setColumna(Integer columna) {
        this.columna = columna;
    }

    public String getNombrePersona() {
        return this.nombrePersona;
    }

    public AsientoSeleccionado nombrePersona(String nombrePersona) {
        this.setNombrePersona(nombrePersona);
        return this;
    }

    public void setNombrePersona(String nombrePersona) {
        this.nombrePersona = nombrePersona;
    }

    public Sesion getSesion() {
        return this.sesion;
    }

    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    public AsientoSeleccionado sesion(Sesion sesion) {
        this.setSesion(sesion);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AsientoSeleccionado)) {
            return false;
        }
        return getId() != null && getId().equals(((AsientoSeleccionado) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AsientoSeleccionado{" +
            "id=" + getId() +
            ", fila=" + getFila() +
            ", columna=" + getColumna() +
            ", nombrePersona='" + getNombrePersona() + "'" +
            "}";
    }
}
