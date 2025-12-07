package com.evento.backend.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller para manejar notificaciones de sincronización desde el Proxy.
 * <p>
 * Este controller recibe notificaciones cuando el Proxy detecta cambios en eventos
 * desde Kafka. Por ahora (Fase 2) solo loguea las solicitudes. La sincronización
 * real con el servidor de la cátedra se implementará en Fase 3.
 */
@RestController
@RequestMapping("/api/eventos")
public class EventoSyncResource {

    private static final Logger log = LoggerFactory.getLogger(EventoSyncResource.class);

    /**
     * POST /api/eventos/sincronizar-todo : Recibe notificación de sincronización desde Proxy.
     * <p>
     * Este endpoint es invocado por el Proxy cuando recibe un mensaje genérico de Kafka
     * indicando "Cambios en los datos de eventos". El mensaje no especifica qué eventos
     * cambiaron, por lo que requiere una sincronización completa.
     * <p>
     * FASE 2: Solo loguea la solicitud y responde OK.
     * <p>
     * TODO FASE 3: Implementar sincronización real:
     * - Consultar todos los eventos desde servidor cátedra (GET /api/endpoints/v1/eventos)
     * - Comparar con eventos en base de datos local
     * - Actualizar/crear/desactivar eventos según corresponda
     * - Consultar Redis para sincronizar estado de asientos
     * - Manejar errores y reintentos
     *
     * @return ResponseEntity con status 200 OK
     */
    @PostMapping("/sincronizar-todo")
    public ResponseEntity<Void> sincronizarTodo() {
        log.info("=================================================");
        log.info("📥 SOLICITUD DE SINCRONIZACIÓN COMPLETA RECIBIDA");
        log.info("Origen: Proxy (notificación de Kafka)");
        log.info("Tópico Kafka: eventos-actualizacion");
        log.info("Mensaje: 'Cambios en los datos de eventos'");
        log.info("=================================================");

        // TODO Fase 3: Implementar lógica de sincronización
        // EventoSyncService.sincronizarTodosLosEventos()

        log.info("✅ Solicitud procesada (sin acción real - Fase 2)");
        log.debug("Fase 3: Aquí se sincronizarán eventos desde cátedra");

        return ResponseEntity.ok().build();
    }
}
