package com.um.trabajofinal.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers("/api/authenticate", "/api/v1/agregar_usuario", "/api/endpoints/v1/eventos-resumidos").permitAll()
            .anyRequest().authenticated();
        return http.build();
    }
}
