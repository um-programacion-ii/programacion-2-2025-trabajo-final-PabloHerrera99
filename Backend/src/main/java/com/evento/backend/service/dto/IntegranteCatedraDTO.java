package com.evento.backend.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IntegranteCatedraDTO {

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("apellido")
    private String apellido;

    @JsonProperty("identificacion")
    private String identificacion;
    // ==================== CONSTRUCTORES ====================

    public IntegranteCatedraDTO() {
        // Constructor vacío para Jackson
    }

    public IntegranteCatedraDTO(String nombre, String apellido, String identificacion) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.identificacion = identificacion;
    }
    // ==================== GETTERS Y SETTERS ====================


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }
    // ==================== MÉTODOS AUXILIARES ====================

    @Override
    public String toString() {
        return "IntegranteCatedraDTO{" +
            "nombre='" + nombre + '\'' +
            ", apellido='" + apellido + '\'' +
            ", identificacion='" + identificacion + '\'' +
            '}';
    }
}
