package com.evento.proxy.web.rest;

import com.evento.proxy.service.ConsultarAsientosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for proxying requests to Cátedra services.
 */
@RestController
@RequestMapping("/api/proxy")
public class ProxyController {

    private static final Logger log = LoggerFactory.getLogger(ProxyController.class);

    private final ConsultarAsientosService consultarAsientosService;

    public ProxyController(ConsultarAsientosService consultarAsientosService) {
        this.consultarAsientosService = consultarAsientosService;
    }

    /**
     * GET /api/proxy/asientos/:eventoId : Get seat availability for an event.
     *
     * @param eventoId the ID of the event to query
     * @return the ResponseEntity with status 200 (OK) and seat data in body,
     *         or status 404 (Not Found) if the event doesn't exist
     */
    @GetMapping(value = "/asientos/{eventoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> obtenerAsientos(@PathVariable Long eventoId) {
        log.debug("REST request to get seats for event: {}", eventoId);
        
        try {
            String asientos = consultarAsientosService.obtenerAsientos(eventoId);
            
            if (asientos == null) {
                log.debug("Event {} not found in Redis", eventoId);
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(asientos);
                
        } catch (Exception e) {
            log.error("Error retrieving seats for event {}: {}", eventoId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
