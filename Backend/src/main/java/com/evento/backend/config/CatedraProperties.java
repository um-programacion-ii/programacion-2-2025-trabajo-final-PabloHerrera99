package com.evento.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "catedra.api")
public class CatedraProperties {
    /**
     * Default: http://192.168.194.250:8080
     */
    private String baseUrl = "http://192.168.194.250:8080";

    /**
     * Usuario para autenticación con el API de cátedra.
     * Se obtiene al registrarse en el servidor.
     */
    private String username;

    /**
     * Contraseña del usuario.
     */
    private String password;
    // Getters y Setters (Spring Boot los usa para inyectar valores)

    public String getBaseUrl() {
        return baseUrl;
    }
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
