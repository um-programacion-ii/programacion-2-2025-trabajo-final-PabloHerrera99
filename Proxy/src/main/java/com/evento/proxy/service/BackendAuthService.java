package com.evento.proxy.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.Instant;

/**
 * Servicio para autenticarse con el Backend y obtener JWT.
 *
 * Responsabilidades:
 * - Login con credenciales de servicio (proxy-service)
 * - Cachear token mientras sea válido
 * - Renovar token automáticamente cuando expire
 */
@Service
public class BackendAuthService {
    private static final Logger log = LoggerFactory.getLogger(BackendAuthService.class);
    @Value("${application.backend.base-url}")
    private String backendBaseUrl;
    @Value("${application.backend.service-username}")
    private String serviceUsername;
    @Value("${application.backend.service-password}")
    private String servicePassword;
    private final RestTemplate restTemplate;

    // Cache del token
    private String cachedToken;
    private Instant tokenExpiration;
    public BackendAuthService() {
        this.restTemplate = new RestTemplate();
    }
    /**
     * Obtiene un token JWT válido.
     * Si el token en cache es válido, lo retorna.
     * Si expiró o no existe, hace login y obtiene uno nuevo.
     */
    public String getValidToken() {
        // Si tenemos token en cache y no ha expirado, usarlo
        if (cachedToken != null && tokenExpiration != null) {
            if (Instant.now().isBefore(tokenExpiration.minusSeconds(60))) {
                log.debug("Usando token en cache (expira en {})", tokenExpiration);
                return cachedToken;
            } else {
                log.debug("Token en cache expirado, renovando...");
            }
        }
        // Obtener nuevo token
        return login();
    }
    /**
     * Hace login en el Backend y obtiene JWT.
     */
    private String login() {
        String url = backendBaseUrl + "/api/authenticate";

        try {
            log.info("Autenticando con Backend como servicio...");
            // Crear request body
            LoginRequest loginRequest = new LoginRequest(serviceUsername, servicePassword);
            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);
            // POST al endpoint de autenticación
            ResponseEntity<LoginResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                LoginResponse.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String token = response.getBody().getIdToken();

                // Cachear token (expira en 24h, configurar en cache por 23h para estar seguros)
                cachedToken = token;
                tokenExpiration = Instant.now().plusSeconds(23 * 60 * 60); // 23 horas

                log.info("Autenticación exitosa - Token obtenido");
                log.debug("Token expirará en cache: {}", tokenExpiration);

                return token;
            } else {
                log.error("Backend respondió sin token: {}", response.getStatusCode());
                throw new RuntimeException("No se pudo obtener token del Backend");
            }
        } catch (Exception e) {
            log.error("Error al autenticarse con Backend: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo al autenticarse con Backend", e);
        }
    }
    /**
     * DTO para request de login
     */
    private static class LoginRequest {
        private String username;
        private String password;
        private boolean rememberMe = false;
        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
        public String getUsername() {
            return username;
        }
        public String getPassword() {
            return password;
        }
        public boolean isRememberMe() {
            return rememberMe;
        }
    }
    /**
     * DTO para response de login
     */
    private static class LoginResponse {
        @JsonProperty("id_token")
        private String idToken;
        public String getIdToken() {
            return idToken;
        }
        public void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
