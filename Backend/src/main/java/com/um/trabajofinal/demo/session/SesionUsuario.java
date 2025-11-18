package com.um.trabajofinal.demo.session;

import com.um.trabajofinal.demo.domain.*;
import com.um.trabajofinal.demo.session.dto.*;
import com.um.trabajofinal.demo.session.enums.PasoFlujo;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SesionUsuario implements Serializable {

    private String sessionId;

    private Usuario usuario;

    private PasoFlujo pasoActual;

    private Evento eventoSeleccionado;

    private Set<AsientoSeleccionadoDto> asientosSeleccionados;

    private List<PersonaDto> personas;
}

