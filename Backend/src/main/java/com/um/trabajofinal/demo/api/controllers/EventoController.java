package com.um.trabajofinal.demo.api.controllers;

import com.um.trabajofinal.demo.api.dto.EventoResumeDto;
import com.um.trabajofinal.demo.service.evento.EventoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/endpoints/v1")
public class EventoController {

  private final EventoService eventoService;

  public EventoController(EventoService eventoService) {
    this.eventoService = eventoService;
  }

  @GetMapping("/eventos-resumidos")
  public List<EventoResumeDto> getEventosResumidos() {
    return eventoService.getEventosResumidos();
  }
}
