package com.um.trabajofinal.demo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "eventos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID que viene del servicio de la cátedra, si corresponde
     */
    @Column(name = "external_id", length = 100, unique = true)
    private String externalId;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @Column(name = "categoria", length = 100)
    private String categoria; // concierto, teatro, etc.

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "lugar", length = 200)
    private String lugar;

    @Column(name = "sala", length = 100)
    private String sala;

    @Column(name = "precio_base", precision = 12, scale = 2)
    private BigDecimal precioBase;

    @Column(name = "moneda", length = 10)
    private String moneda; // "ARS", "USD", etc.

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @PrePersist
    public void prePersist() {
        activo = true;
    }
}
