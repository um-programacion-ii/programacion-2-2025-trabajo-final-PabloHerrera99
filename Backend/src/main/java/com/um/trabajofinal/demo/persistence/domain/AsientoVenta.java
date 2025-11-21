package com.um.trabajofinal.demo.persistence.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "asientos_venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsientoVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Venta a la que pertenece este asiento
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @Column(name = "fila", nullable = false, length = 10)
    private String fila;

    @Column(name = "columna", nullable = false, length = 10)
    private String columna;

    @Column(name = "nombre_persona", length = 200)
    private String nombrePersona;
}

