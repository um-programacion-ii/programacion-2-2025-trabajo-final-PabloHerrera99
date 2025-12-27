package com.evento.backend.web.rest;

import com.evento.backend.service.AsientosDisponibilidadService;
import com.evento.backend.service.dto.MatrizAsientosDTO;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * REST controller para consultar disponibilidad de asientos
 *
 * Endpoint consumido por la aplicación móvil
 */
@RestController
@RequestMapping("/api/eventos")
public class AsientosDisponibilidadResource {
    private static final Logger log = LoggerFactory.getLogger(AsientosDisponibilidadResource.class);
    private final AsientosDisponibilidadService asientosDisponibilidadService;
    public AsientosDisponibilidadResource(AsientosDisponibilidadService service) {
        this.asientosDisponibilidadService = service;
    }
    /**
     * GET /api/eventos/{id}/asientos/disponibilidad
     *
     * Obtiene la matriz completa de asientos con disponibilidad en tiempo real
     *
     * Respuesta incluye:
     * - Información del evento
     * - Matriz completa de asientos (fila, columna, estado)
     * - Estadísticas (disponibles, bloqueados, vendidos)
     * - Timestamp de consulta
     *
     * Estados posibles de asiento:
     * - DISPONIBLE: Puede ser seleccionado
     * - BLOQUEADO: Bloqueado temporalmente por otro usuario (expira en 5 min)
     * - VENDIDO: Ya fue vendido
     *
     * @param id - ID local del evento
     * @return 200 OK con MatrizAsientosDTO
     *         404 Not Found si evento no existe
     *         400 Bad Request si evento inactivo o sin configuración
     *         500 Internal Server Error si Proxy no disponible
     */
    @GetMapping("/{id}/asientos/disponibilidad")
    public ResponseEntity<MatrizAsientosDTO> getDisponibilidadAsientos(
        @PathVariable Long id
    ) {
        log.info("REST request GET /api/eventos/{}/asientos/disponibilidad", id);
        try {
            MatrizAsientosDTO matriz = asientosDisponibilidadService
                .getDisponibilidadAsientos(id);
            return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(matriz);
        } catch (EntityNotFoundException e) {
            log.warn("Evento no encontrado: {}", id);
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.warn("Estado inválido para evento {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            // Típicamente error de conectividad con Proxy
            log.error("Error obteniendo disponibilidad para evento {}: {}",
                id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
