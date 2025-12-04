package com.evento.proxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Consumidor de Kafka que escucha el tópico 'eventos-actualizacion'
 * del servidor de la cátedra.
 * <p>
 * Recibe notificaciones cuando:
 * - Se crea un evento nuevo
 * - Se modifica un evento existente
 * - Se cancela un evento
 * - Un evento expira
 */
@Service
public class EventoKafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(EventoKafkaConsumer.class);

    /**
     * Método que se ejecuta automáticamente cuando llega un mensaje
     * al tópico 'eventos-actualizacion'.
     *
     * @param mensaje Contenido del mensaje en formato JSON (String)
     */
    @KafkaListener(topics = "eventos-actualizacion", groupId = "${spring.kafka.consumer.group-id}")
    public void consumirEventoActualizacion(String mensaje) {
        log.info("==================================================");
        log.info("🔔 MENSAJE RECIBIDO DE KAFKA");
        log.info("Tópico: eventos-actualizacion");
        log.info("Contenido: {}", mensaje);
        log.info("==================================================");

        try {
            log.debug("Mensaje procesado correctamente");
        } catch (Exception e) {
            log.error("Error al procesar mensaje de Kafka: {}", e.getMessage(), e);
        }
    }
}
