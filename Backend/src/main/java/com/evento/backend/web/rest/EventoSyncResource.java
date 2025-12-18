package com.evento.backend.web.rest;

import com.evento.backend.service.EventoSyncService;
import com.evento.backend.service.EventoSyncService.SyncResult;
import com.evento.backend.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller para manejar notificaciones de sincronización desde el Proxy.
 */
@RestController
@RequestMapping("/api/eventos")
public class EventoSyncResource {

    private static final Logger log = LoggerFactory.getLogger(EventoSyncResource.class);

    private final EventoSyncService eventSyncService;  // NUEVO

    public EventoSyncResource(EventoSyncService eventSyncService) {
        this.eventSyncService = eventSyncService;
    }
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

        String caller = SecurityUtils.getCurrentUserLogin().orElse("unknown");

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
