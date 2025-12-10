package com.evento.backend.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.Instant;

public class RedisAsientoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonProperty("fila")
    private Integer fila;
    @JsonProperty("columna")
    private Integer columna;
    @JsonProperty("estado")
    private String estado;  // "Bloqueado" o "Vendido"
    @JsonProperty("expira")
    private Instant expira;  // Solo para estado "Bloqueado"
    @JsonProperty("nombrePersona")
    private String nombrePersona;
    // Constructor vacío
    public RedisAsientoDTO() {
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
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
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
        return "RedisAsientoDTO{" +
            "fila=" + fila +
            ", columna=" + columna +
            ", estado='" + estado + '\'' +
            ", expira=" + expira +
            ", nombrePersona='" + nombrePersona + '\'' +
            '}';
    }
}
