package com.evento.backend.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller para manejar notificaciones de sincronización desde el Proxy.
 *
 * Este controller recibe notificaciones cuando el Proxy detecta cambios en eventos
 * desde Kafka. Por ahora solo loguea las solicitudes.
 */
@RestController
@RequestMapping("/api/eventos")
public class EventoSyncResource {

    private static final Logger log = LoggerFactory.getLogger(EventoSyncResource.class);

    /**
     * POST /api/eventos/sincronizar-todo : Recibe notificación de sincronización desde Proxy.
     *
     * Este endpoint es invocado por el Proxy cuando recibe un mensaje genérico de Kafka
     * indicando "Cambios en los datos de eventos". El mensaje no especifica qué eventos
     * cambiaron, por lo que requiere una sincronización completa.
     *
     * @return ResponseEntity con status 200 OK
     */
    @PostMapping("/sincronizar-todo")
    public ResponseEntity<Void> sincronizarTodo() {
        log.info("=================================================");
        log.info("SOLICITUD DE SINCRONIZACIÓN COMPLETA RECIBIDA");
        log.info("Origen: Proxy (notificación de Kafka)");
        log.info("Tópico Kafka: eventos-actualizacion");
        log.info("Mensaje: 'Cambios en los datos de eventos'");
        log.info("=================================================");

        // Proximo: Implementar lógica de sincronización

        log.info("Solicitud procesada (sin acción real)");
        log.debug("Aquí se sincronizarán eventos desde cátedra");

        return ResponseEntity.ok().build();
    }
}
