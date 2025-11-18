package com.um.trabajofinal.demo.session.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonaDto implements Serializable {

    private String nombreCompleto;
    private String documento;  // si hace falta
}

