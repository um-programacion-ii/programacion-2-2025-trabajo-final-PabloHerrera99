package com.um.trabajofinal.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

import com.um.trabajofinal.demo.persistence.repository.UsuarioRepository;
import com.um.trabajofinal.demo.persistence.domain.Usuario;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner dataLoader(UsuarioRepository usuarioRepository) {
		return args -> {
			long count = usuarioRepository.count();
			System.out.println("[SeedCheck] Usuarios en BD: " + count);
			usuarioRepository.findAll().forEach(u -> {
				System.out.println("[SeedCheck] Usuario: " + u.getUsername());
			});
		};
	}
}
