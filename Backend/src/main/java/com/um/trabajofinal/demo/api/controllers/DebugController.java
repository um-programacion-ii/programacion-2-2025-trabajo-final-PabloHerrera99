package com.um.trabajofinal.demo.api.controllers;

import com.um.trabajofinal.demo.persistence.domain.Usuario;
import com.um.trabajofinal.demo.persistence.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

  private final UsuarioRepository usuarioRepository;

  public DebugController(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  @GetMapping("/users")
  public List<String> listUsers() {
    return usuarioRepository.findAll().stream().map(Usuario::getUsername).collect(Collectors.toList());
  }
}
