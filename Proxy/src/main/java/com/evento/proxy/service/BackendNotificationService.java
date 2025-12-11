package com.evento.proxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
/**
 * Servicio para notificar al Backend cuando hay cambios en eventos (vía Kafka).
 *
 * Responsabilidades:
 * - Recibir señal de EventoKafkaConsumer
 * - Hacer POST al Backend para trigger sincronización
 * - Manejo de errores de comunicación
 */
@Service
public class BackendNotificationService {

    private static final Logger log = LoggerFactory.getLogger(BackendNotificationService.class);

    @Value("${application.backend.base-url}")
    private String backendBaseUrl;

    private final RestTemplate restTemplate;

    public BackendNotificationService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Notifica al Backend que debe sincronizar todos los eventos.
     * Llamado cuando Kafka consumer recibe mensaje de actualización.
     */
    public void notificarSincronizacionCompleta() {
        log.info("INICIANDO NOTIFICACIÓN AL BACKEND");

        String url = backendBaseUrl + "/api/eventos/sincronizar-todo";

        try {
            log.debug("URL destino: {}", url);

            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Crear request vacío (endpoint no requiere body)
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // Hacer POST al Backend
            log.info("Enviando POST a Backend: {}", url);

            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
            );

            // Log resultado
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Backend notificado exitosamente");
                log.info("  Status: {}", response.getStatusCode());
                log.debug("  Response: {}", response.getBody());
            } else {
                log.warn("Backend respondió con status no exitoso: {}", response.getStatusCode());
            }

        } catch (HttpClientErrorException e) {
            log.error("✗ Error HTTP del Backend (4xx): {} - {}",
                e.getStatusCode(), e.getResponseBodyAsString());
            log.error("  No se pudo completar la sincronización");

        } catch (HttpServerErrorException e) {
            log.error("✗ Error interno del Backend (5xx): {} - {}",
                e.getStatusCode(), e.getResponseBodyAsString());
            log.error("  Backend no pudo procesar la sincronización");

        } catch (ResourceAccessException e) {
            log.error("✗ Backend no disponible (timeout/connection refused)");
            log.error("  Mensaje: {}", e.getMessage());
            log.error("  Verificar que Backend esté corriendo en: {}", backendBaseUrl);

        } catch (Exception e) {
            log.error("Error inesperado al notificar Backend: {}", e.getMessage(), e);
        }


        log.info("NOTIFICACIÓN COMPLETADA");

    }
}
