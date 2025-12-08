package com.evento.proxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventoKafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(EventoKafkaConsumer.class);
    private final BackendNotificationService backendNotificationService;

    public EventoKafkaConsumer(BackendNotificationService backendNotificationService) {
        this.backendNotificationService = backendNotificationService;
    }

    @KafkaListener(topics = "eventos-actualizacion", groupId = "${spring.kafka.consumer.group-id}")
    public void consumirEventoActualizacion(String mensaje) {
        log.info("==================================================");
        log.info("MENSAJE RECIBIDO DE KAFKA");
        log.info("Tópico: eventos-actualizacion");
        log.info("Contenido: {}", mensaje);
        log.info("==================================================");
        if (esMensajeGenerico(mensaje)) {
            log.info("Mensaje genérico detectado: Requiere sincronización completa");
            backendNotificationService.notificarSincronizacionCompleta();
            log.info("Solicitud de sincronización procesada");
        } else {
            log.warn("Mensaje no reconocido (se esperaba: 'Cambios en los datos de eventos')");
            log.warn("Mensaje recibido: {}", mensaje);
        }
    }

    private boolean esMensajeGenerico(String mensaje) {
        if (mensaje == null || mensaje.isBlank()) {
            return false;
        }

        String mensajeNormalizado = mensaje.trim();

        // Detectar mensaje genérico de la cátedra
        return mensajeNormalizado.equals("Cambios en los datos de eventos");
    }
}
