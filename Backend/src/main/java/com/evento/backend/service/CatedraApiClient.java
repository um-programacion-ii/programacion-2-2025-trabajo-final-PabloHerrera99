package com.evento.backend.service;

import com.evento.backend.config.CatedraProperties;
import com.evento.backend.service.dto.EventoCatedraDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 * Cliente HTTP para consumir el API REST del servidor de cátedra.
 *
 * Responsabilidades:
 * - Hacer llamadas HTTP GET al endpoint de eventos
 * - Agregar header Authorization con token JWT
 * - Deserializar respuestas JSON a objetos EventoCatedraDTO
 * - Manejar errores de autenticación (401) renovando el token
 *
 * Este servicio es usado por EventoSyncService para obtener eventos
 * desde el servidor de cátedra durante la sincronización.
 *
 * Endpoints consumidos:
 * - GET /api/endpoints/v1/eventos (lista completa de eventos activos)
 */
@Service
public class CatedraApiClient {
    private static final Logger log = LoggerFactory.getLogger(CatedraApiClient.class);
    private final CatedraProperties catedraProperties;
    private final CatedraAuthService authService;
    private final RestTemplate restTemplate;

    public CatedraApiClient(
        CatedraProperties catedraProperties,
        CatedraAuthService authService
    ) {
        this.catedraProperties = catedraProperties;
        this.authService = authService;
        this.restTemplate = new RestTemplate();
    }

    public List<EventoCatedraDTO> getEventosCompletos() {
        String url = catedraProperties.getBaseUrl() + "/api/endpoints/v1/eventos";

        try {
            // Primer intento con token actual
            log.debug("Consultando eventos desde cátedra: {}", url);
            return fetchEventos(url);

        } catch (HttpClientErrorException e) {
            // Verificar si es error de autenticación (token expirado)
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                log.warn("Token expirado (401 Unauthorized), renovando...");
                authService.renewToken();

                // Segundo intento con token renovado
                log.debug("Reintentando consulta con token renovado...");
                return fetchEventos(url);

            } else {
                // Otro tipo de error HTTP (403, 404, 500, etc.)
                log.error("Error HTTP al obtener eventos: {} - {}",
                    e.getStatusCode(), e.getMessage());
                throw new RuntimeException("Error al consultar eventos desde cátedra", e);
            }

        } catch (Exception e) {
            // Error de red, timeout, o error inesperado
            log.error("Error inesperado al obtener eventos: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<EventoCatedraDTO> fetchEventos(String url) {
        // 1. Obtener token JWT válido
        String token = authService.getValidToken();

        // 2. Construir headers HTTP con Authorization Bearer
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        // 3. Crear request HTTP (solo headers, sin body en GET)
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // 4. Hacer GET y deserializar respuesta JSON a array de DTOs
        log.debug("   Headers: Authorization: Bearer {}...", token.substring(0, 20));

        ResponseEntity<EventoCatedraDTO[]> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            EventoCatedraDTO[].class  // Jackson deserializa JSON array → DTO[]
        );

        // 5. Convertir array a List y devolver
        if (response.getBody() != null) {
            List<EventoCatedraDTO> eventos = Arrays.asList(response.getBody());
            log.info("Eventos obtenidos desde cátedra: {}", eventos.size());

            // Debug: Mostrar títulos de eventos (útil para verificar)
            if (log.isDebugEnabled()) {
                eventos.forEach(e ->
                    log.debug("   - Evento ID {}: {}", e.getId(), e.getTitulo())
                );
            }

            return eventos;
        }

        // Respuesta vacía (no debería pasar, pero por si acaso)
        log.warn("Respuesta vacía del servidor de cátedra");
        return Collections.emptyList();
    }
}
