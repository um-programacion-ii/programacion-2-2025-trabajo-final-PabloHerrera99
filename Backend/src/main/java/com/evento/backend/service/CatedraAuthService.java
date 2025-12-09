package com.evento.backend.service;

import com.evento.backend.config.CatedraProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
/**
 * Servicio para manejar la autenticación con el API de cátedra.
 *
 * Responsabilidades:
 * - Realizar login contra el servidor de cátedra
 * - Mantener un token JWT en memoria (cache simple)
 * - Renovar el token cuando expira (detectado por respuesta 401)
 *
 * El token se guarda en memoria mientras la aplicación esté corriendo.
 * Si el Backend se reinicia, se hará login nuevamente automáticamente.
 */
@Service
public class CatedraAuthService {
    private static final Logger log = LoggerFactory.getLogger(CatedraAuthService.class);


    private final CatedraProperties catedraProperties;
    private final RestTemplate restTemplate;

    private String cachedToken;

    public CatedraAuthService(CatedraProperties catedraProperties) {
        this.catedraProperties = catedraProperties;
        this.restTemplate = new RestTemplate();
    }

    public String getValidToken() {
        if (cachedToken == null) {
            log.info("No hay token en cache, realizando login...");
            login();
        }
        return cachedToken;
    }

    public void login() {
        try {
            // 1. Construir URL completa
            String url = catedraProperties.getBaseUrl() + "/api/authenticate";

            // 2. Crear body del request (JSON)
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", catedraProperties.getUsername());
            requestBody.put("password", catedraProperties.getPassword());
            requestBody.put("rememberMe", "false");
            // 3. Crear headers HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // 4. Crear request HTTP (body + headers)
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            // 5. Hacer POST y deserializar respuesta a LoginResponse
            log.debug("Realizando login a: {}", url);
            LoginResponse response = restTemplate.postForObject(url, request, LoginResponse.class);
            // 6. Verificar que obtuvimos el token
            if (response != null && response.getIdToken() != null) {
                cachedToken = response.getIdToken();
                log.info("✓ Login exitoso, token obtenido: {}...", cachedToken.substring(0, 20));
            } else {
                log.error("✗ Login falló: respuesta nula o sin token");
                throw new RuntimeException("No se pudo obtener token de autenticación");
            }
        } catch (Exception e) {
            log.error("✗ Error en login a API de cátedra: {}", e.getMessage());
            throw new RuntimeException("Error de autenticación con servidor de cátedra", e);
        }
    }

    public void renewToken() {
        log.info("♻ Renovando token JWT (token expirado)...");
        cachedToken = null;  // Limpiar cache
        login();             // Hacer login nuevamente
    }

    private static class LoginResponse {

        @JsonProperty("id_token")
        private String idToken;

        public LoginResponse() {}

        public String getIdToken() {
            return idToken;
        }

        public void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
