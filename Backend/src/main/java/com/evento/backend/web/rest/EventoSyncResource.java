package com.evento.backend.web.rest;

import com.evento.backend.service.EventoSyncService;
import com.evento.backend.service.EventoSyncService.SyncResult;
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

    private final EventoSyncService eventSyncService;  // NUEVO

    // NUEVO: Constructor con inyección de dependencias
    public EventoSyncResource(EventoSyncService eventSyncService) {
        this.eventSyncService = eventSyncService;
    }
    /**
     * POST /api/eventos/sincronizar-todo : Recibe notificación de sincronización desde Proxy.
     *
     * Este endpoint es invocado por el Proxy cuando recibe un mensaje genérico de Kafka
     * indicando "Cambios en los datos de eventos". El mensaje no especifica qué eventos
     * cambiaron, por lo que requiere una sincronización completa.
     *
     * @return ResponseEntity con status 200 OK
     */
    /**
     * POST /api/eventos/sincronizar-todo : Sincroniza eventos desde el API de cátedra.
     *
     * Este endpoint puede ser invocado:
     * - Manualmente (ej: curl, Postman)
     * - Automáticamente por el Proxy cuando recibe mensaje de Kafka
     *
     * @return ResponseEntity con SyncResult (contadores de operaciones)
     */
    @PostMapping("/sincronizar-todo")
    public ResponseEntity<EventoSyncService.SyncResult> sincronizarTodo() {
        log.info("=================================================");
        log.info("SOLICITUD DE SINCRONIZACIÓN COMPLETA RECIBIDA");
        log.info("=================================================");

        // Ejecutar sincronización
        SyncResult result = eventSyncService.synchronize();

        // Log de resumen
        log.info("Sincronización finalizada: {} creados, {} actualizados, {} desactivados, {} errores",
            result.getCreated(),
            result.getUpdated(),
            result.getDeactivated(),
            result.getErrors().size());

        return ResponseEntity.ok(result);
    }
}
