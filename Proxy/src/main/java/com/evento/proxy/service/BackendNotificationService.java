package com.evento.proxy.service;

import com.evento.proxy.config.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class BackendNotificationService {

    private static final Logger log = LoggerFactory.getLogger(BackendNotificationService.class);
    private final RestTemplate restTemplate;
    private final ApplicationProperties applicationProperties;

    public BackendNotificationService(
        RestTemplate restTemplate,
        ApplicationProperties applicationProperties
    ) {
        this.restTemplate = restTemplate;
        this.applicationProperties = applicationProperties;
    }
    /**
     * Notifica al Backend que debe sincronizar todos los eventos desde Redis.
     * <p>
     * Este método se invoca cuando se recibe un mensaje genérico de Kafka
     * que indica "Cambios en los datos de eventos" sin especificar qué eventos
     * cambiaron exactamente.
     * <p>
     * El Backend debe:
     * 1. Consultar todos los eventos desde Redis
     * 2. Actualizar su base de datos local
     * 3. Sincronizar todos los datos relacionados (asientos, ventas, etc.)
     */
    public void notificarSincronizacionCompleta() {
        // 1. Construir URL del endpoint
        String backendUrl = applicationProperties.getBackend().getBaseUrl();
        String endpoint = backendUrl + "/api/eventos/sincronizar-todo";

        // 2. Loguear inicio de notificación
        log.info("🔄 Solicitando sincronización completa al Backend");
        log.debug("URL destino: {}", endpoint);

        try {
            // 3. Preparar headers HTTP (sin body, solo POST vacío)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 4. Crear request vacío
            HttpEntity<Void> request = new HttpEntity<>(headers);

            // 5. Enviar POST al Backend
            ResponseEntity<Void> response = restTemplate.postForEntity(
                endpoint,
                request,
                Void.class
            );

            // 6. Verificar respuesta
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Solicitud de sincronización completa enviada exitosamente (status: {})", response.getStatusCode());
            } else {
                log.warn("⚠ Backend respondió con código no exitoso: {}", response.getStatusCode());
            }

        } catch (ResourceAccessException e) {
            // Backend no disponible
            log.warn("⚠ Backend no disponible: {}. La sincronización debe ejecutarse manualmente.", e.getMessage());
            log.debug("Stack trace completo:", e);

            // No lanzamos excepción - el Kafka consumer continúa

        } catch (Exception e) {
            // Cualquier otro error
            log.error("❌ Error inesperado al solicitar sincronización completa: {}", e.getMessage(), e);
        }
    }
}
