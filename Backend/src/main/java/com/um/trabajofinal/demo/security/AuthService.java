package com.um.trabajofinal.demo.security;

import com.um.trabajofinal.demo.persistence.domain.Usuario;
import com.um.trabajofinal.demo.persistence.repository.UsuarioRepository;
import com.um.trabajofinal.demo.api.dto.AuthResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.Base64;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> authenticate(String username, String password) {
        Optional<Usuario> user = usuarioRepository.findByUsername(username);
        // En este mock, asumimos que la password está en claro; en prod usaríamos hash
        return user.filter(u -> u.getPassword() != null && u.getPassword().equals(password));
    }

    public String generateToken(Usuario usuario) {
        // Token simple (no real JWT). Incluye id y timestamp para simulación.
        String plain = usuario.getId() + ":" + Instant.now().toEpochMilli();
        return Base64.getEncoder().encodeToString(plain.getBytes());
    }

    public AuthResponse buildAuthResponse(Usuario usuario, String token) {
        return AuthResponse.builder()
                .token(token)
                .userId(usuario.getId())
                .username(usuario.getUsername())
                .build();
    }
}
