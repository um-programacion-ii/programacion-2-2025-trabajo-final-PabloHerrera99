package com.um.trabajofinal.demo.api.controllers;

import com.um.trabajofinal.demo.persistence.domain.Usuario;
import com.um.trabajofinal.demo.security.AuthService;
import com.um.trabajofinal.demo.api.dto.AuthResponse;
import com.um.trabajofinal.demo.api.dto.LoginRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody LoginRequest loginRequest) {
        Optional<Usuario> user = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        if (user.isPresent()) {
            String token = authService.generateToken(user.get());
            AuthResponse resp = authService.buildAuthResponse(user.get(), token);
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
