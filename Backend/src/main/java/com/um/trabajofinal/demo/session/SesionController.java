package com.um.trabajofinal.demo.session;

import com.um.trabajofinal.demo.persistence.domain.Usuario;
import com.um.trabajofinal.demo.persistence.repository.UsuarioRepository;
import com.um.trabajofinal.demo.api.dto.SessionRequest;
import com.um.trabajofinal.demo.session.SesionUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class SesionController {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public SesionController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/agregar_usuario")
    public SesionUsuario agregarUsuario(@RequestBody SessionRequest request) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(request.getUsername());
        if (usuario.isEmpty()) {
            // Si el usuario no existe, crear uno minimal (con campos requeridos) puede hacerse, pero por ahora fallamos
            throw new RuntimeException("Usuario no encontrado");
        }
        SesionUsuario session = SesionUsuario.builder()
                .sessionId(UUID.randomUUID().toString())
                .usuario(usuario.get())
                .build();
        return session;
    }
}
