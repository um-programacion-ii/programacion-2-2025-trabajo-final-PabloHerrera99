package com.evento.backend.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventoTipoCatedraDTO {

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("descripcion")
    private String descripcion;
    // ==================== CONSTRUCTORES ====================


    public EventoTipoCatedraDTO() {
        // Constructor vacío para Jackson
    }

    public EventoTipoCatedraDTO(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    // ==================== GETTERS Y SETTERS ====================

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    // ==================== MÉTODOS AUXILIARES ====================


    @Override
    public String toString() {
        return "EventoTipoCatedraDTO{" +
            "nombre='" + nombre + '\'' +
            ", descripcion='" + descripcion + '\'' +
            '}';
    }
}
