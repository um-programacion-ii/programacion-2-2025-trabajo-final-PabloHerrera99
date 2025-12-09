package com.evento.backend.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class EventoCatedraDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("titulo")
    private String titulo;

    @JsonProperty("resumen")
    private String resumen;

    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("fecha")
    private Instant fecha;

    @JsonProperty("direccion")
    private String direccion;

    @JsonProperty("imagen")
    private String imagen;

    @JsonProperty("filaAsientos")
    private Integer filaAsientos;

    @JsonProperty("columnAsientos")
    private Integer columnaAsientos;

    @JsonProperty("precioEntrada")
    private BigDecimal precioEntrada;

    @JsonProperty("eventoTipo")
    private EventoTipoCatedraDTO eventoTipo;

    @JsonProperty("integrantes")
    private List<IntegranteCatedraDTO> integrantes;
    // ==================== CONSTRUCTORES ====================


    public EventoCatedraDTO() {
        // Constructor vacío para Jackson
    }
    // ==================== GETTERS Y SETTERS ====================


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public EventoTipoCatedraDTO getEventoTipo() {
        return eventoTipo;
    }
    public void setEventoTipo(EventoTipoCatedraDTO eventoTipo) {
        this.eventoTipo = eventoTipo;
    }
    public List<IntegranteCatedraDTO> getIntegrantes() {
        return integrantes;
    }
    public void setIntegrantes(List<IntegranteCatedraDTO> integrantes) {
        this.integrantes = integrantes;
    }
    // ==================== MÉTODOS AUXILIARES ====================

    @Override
    public String toString() {
        return "EventoCatedraDTO{" +
            "id=" + id +
            ", titulo='" + titulo + '\'' +
            ", fecha=" + fecha +
            ", eventoTipo=" + eventoTipo +
            ", cantidadIntegrantes=" + (integrantes != null ? integrantes.size() : 0) +
            '}';
    }
}
