package com.um.trabajofinal.demo.session.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsientoSeleccionadoDto implements Serializable {

    private String fila;
    private String columna;
}

