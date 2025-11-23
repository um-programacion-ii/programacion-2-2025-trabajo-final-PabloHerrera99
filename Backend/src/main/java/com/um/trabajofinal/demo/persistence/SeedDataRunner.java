package com.um.trabajofinal.demo.persistence;

import com.um.trabajofinal.demo.persistence.domain.Usuario;
import com.um.trabajofinal.demo.persistence.domain.enums.RolUsuario;
import com.um.trabajofinal.demo.persistence.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDateTime;

@Configuration
public class SeedDataRunner {

    @Bean
    public CommandLineRunner seedUsuarios(UsuarioRepository usuarioRepository) {
        return args -> {
            if (usuarioRepository.findByUsername("usuario").isEmpty()) {
                Usuario u = Usuario.builder()
                        .username("usuario")
                        .password("passwd")
                        .rol(RolUsuario.ROLE_USUARIO)
                        .habilitado(true)
                        .creadoEn(LocalDateTime.now())
                        .build();
                usuarioRepository.save(u);
                System.out.println("SeedDataRunner: seed usuario 'usuario' creado");
            } else {
                System.out.println("SeedDataRunner: seed usuario ya existente");
            }
        };
    }
}
