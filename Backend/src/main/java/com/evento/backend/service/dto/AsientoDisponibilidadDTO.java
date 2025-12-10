package com.evento.backend.service.dto;

import com.evento.backend.domain.enumeration.EstadoAsiento;

import java.time.Instant;

public class AsientoDisponibilidadDTO {
    private static final long serialVersionUID = 1L;
    /**
     * Número de fila (1-based)
     */
    private Integer fila;
    /**
     * Número de columna (1-based)
     */
    private Integer columna;
    /**
     * Estado actual del asiento
     */
    private EstadoAsiento estado;
    /**
     * Timestamp de expiración del bloqueo (solo para estado BLOQUEADO)
     */
    private Instant expira;
    /**
     * Nombre de la persona (solo para estado VENDIDO o BLOQUEADO)
     */
    private String nombrePersona;
    // Constructors
    public AsientoDisponibilidadDTO() {
    }
    public AsientoDisponibilidadDTO(Integer fila, Integer columna, EstadoAsiento estado) {
        this.fila = fila;
        this.columna = columna;
        this.estado = estado;
    }
    // Getters and Setters
    public Integer getFila() {
        return fila;
    }
    public void setFila(Integer fila) {
        this.fila = fila;
    }
    public Integer getColumna() {
        return columna;
    }
    public void setColumna(Integer columna) {
        this.columna = columna;
    }
    public EstadoAsiento getEstado() {
        return estado;
    }
    public void setEstado(EstadoAsiento estado) {
        this.estado = estado;
    }
    public Instant getExpira() {
        return expira;
    }
    public void setExpira(Instant expira) {
        this.expira = expira;
    }
    public String getNombrePersona() {
        return nombrePersona;
    }
    public void setNombrePersona(String nombrePersona) {
        this.nombrePersona = nombrePersona;
    }
    @Override
    public String toString() {
        return "AsientoDisponibilidadDTO{" +
            "fila=" + fila +
            ", columna=" + columna +
            ", estado=" + estado +
            ", expira=" + expira +
            ", nombrePersona='" + nombrePersona + '\'' +
            '}';
    }
}
