package com.evento.backend.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RedisResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonProperty("eventoId")
    private Long eventoId;
    @JsonProperty("asientos")
    private List<RedisAsientoDTO> asientos = new ArrayList<>();
    // Constructor vacío
    public RedisResponseDTO() {
    }
    // Getters and Setters
    public Long getEventoId() {
        return eventoId;
    }
    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }
    public List<RedisAsientoDTO> getAsientos() {
        return asientos;
    }
    public void setAsientos(List<RedisAsientoDTO> asientos) {
        this.asientos = asientos;
    }
    @Override
    public String toString() {
        return "RedisResponseDTO{" +
            "eventoId=" + eventoId +
            ", asientos=" + (asientos != null ? asientos.size() : 0) +
            '}';
    }
}
