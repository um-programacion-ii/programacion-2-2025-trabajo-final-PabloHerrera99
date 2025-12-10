package com.evento.backend.service.dto;

import java.time.Instant;
import java.util.List;

public class MatrizAsientosDTO {
    private static final long serialVersionUID = 1L;
    /**
     * ID local del evento en BD Backend
     */
    private Long eventoId;
    /**
     * ID del evento en servidor de cátedra
     */
    private Long eventoIdCatedra;
    /**
     * Título del evento
     */
    private String tituloEvento;
    /**
     * Cantidad total de filas
     */
    private Integer totalFilas;
    /**
     * Cantidad total de columnas
     */
    private Integer totalColumnas;
    /**
     * Cantidad total de asientos (filas * columnas)
     */
    private Integer totalAsientos;
    /**
     * Cantidad de asientos disponibles
     */
    private Integer disponibles;
    /**
     * Cantidad de asientos bloqueados (no expirados)
     */
    private Integer bloqueados;
    /**
     * Cantidad de asientos vendidos
     */
    private Integer vendidos;
    /**
     * Lista completa de asientos con su disponibilidad
     */
    private List<AsientoDisponibilidadDTO> asientos;
    /**
     * Timestamp de cuando se consultó esta información
     */
    private Instant consultadoEn;
    // Constructor vacío
    public MatrizAsientosDTO() {
    }
    // Getters and Setters
    public Long getEventoId() {
        return eventoId;
    }
    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }
    public Long getEventoIdCatedra() {
        return eventoIdCatedra;
    }
    public void setEventoIdCatedra(Long eventoIdCatedra) {
        this.eventoIdCatedra = eventoIdCatedra;
    }
    public String getTituloEvento() {
        return tituloEvento;
    }
    public void setTituloEvento(String tituloEvento) {
        this.tituloEvento = tituloEvento;
    }
    public Integer getTotalFilas() {
        return totalFilas;
    }
    public void setTotalFilas(Integer totalFilas) {
        this.totalFilas = totalFilas;
    }
    public Integer getTotalColumnas() {
        return totalColumnas;
    }
    public void setTotalColumnas(Integer totalColumnas) {
        this.totalColumnas = totalColumnas;
    }
    public Integer getTotalAsientos() {
        return totalAsientos;
    }
    public void setTotalAsientos(Integer totalAsientos) {
        this.totalAsientos = totalAsientos;
    }
    public Integer getDisponibles() {
        return disponibles;
    }
    public void setDisponibles(Integer disponibles) {
        this.disponibles = disponibles;
    }
    public Integer getBloqueados() {
        return bloqueados;
    }
    public void setBloqueados(Integer bloqueados) {
        this.bloqueados = bloqueados;
    }
    public Integer getVendidos() {
        return vendidos;
    }
    public void setVendidos(Integer vendidos) {
        this.vendidos = vendidos;
    }
    public List<AsientoDisponibilidadDTO> getAsientos() {
        return asientos;
    }
    public void setAsientos(List<AsientoDisponibilidadDTO> asientos) {
        this.asientos = asientos;
    }
    public Instant getConsultadoEn() {
        return consultadoEn;
    }
    public void setConsultadoEn(Instant consultadoEn) {
        this.consultadoEn = consultadoEn;
    }
    @Override
    public String toString() {
        return "MatrizAsientosDTO{" +
            "eventoId=" + eventoId +
            ", eventoIdCatedra=" + eventoIdCatedra +
            ", tituloEvento='" + tituloEvento + '\'' +
            ", totalFilas=" + totalFilas +
            ", totalColumnas=" + totalColumnas +
            ", totalAsientos=" + totalAsientos +
            ", disponibles=" + disponibles +
            ", bloqueados=" + bloqueados +
            ", vendidos=" + vendidos +
            ", cantidadAsientos=" + (asientos != null ? asientos.size() : 0) +
            ", consultadoEn=" + consultadoEn +
            '}';
    }
}
