package com.evento.backend.web.rest;

import com.evento.backend.security.SecurityUtils;
import com.evento.backend.service.SesionBusinessService;
import com.evento.backend.service.dto.*;
import com.evento.backend.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller para gestión del flujo de compra de asientos.
 *
 * Endpoints:
 * - POST   /api/compra/iniciar              - Inicia nueva sesión de compra
 * - GET    /api/compra/estado               - Obtiene estado de sesión actual
 * - POST   /api/compra/actividad            - Keep-alive de sesión
 * - POST   /api/compra/seleccionar-asientos - Selecciona y bloquea asientos
 * - POST   /api/compra/asignar-nombres      - Asigna nombres a asientos
 * - POST   /api/compra/confirmar            - Confirma la venta
 * - DELETE /api/compra/cancelar             - Cancela sesión activa
 */
@RestController
@RequestMapping("/api/compra")
public class SesionCompraResource {
    private static final Logger LOG = LoggerFactory.getLogger(SesionCompraResource.class);
    private static final String ENTITY_NAME = "sesion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;
    private final SesionBusinessService sesionBusinessService;
    public SesionCompraResource(SesionBusinessService sesionBusinessService) {
        this.sesionBusinessService = sesionBusinessService;
    }

    /**
     * {@code POST  /compra/iniciar} : Inicia una nueva sesión de compra.
     * Si el usuario ya tiene una sesión activa, la cancela automáticamente.
     * @param request el DTO con el eventoId
     * @return ResponseEntity con el SesionDTO creado
     */
    @PostMapping("/iniciar")
    public ResponseEntity<SesionDTO> iniciarSesion(@Valid @RequestBody IniciarSesionRequestDTO request) {
        LOG.debug("REST request to iniciar sesión de compra: {}", request);

        Long userId = SecurityUtils.getCurrentUserId()
            .orElseThrow(() -> new BadRequestAlertException(
                "Usuario no autenticado",
                ENTITY_NAME,
                "notauthenticated"
            ));
        SesionDTO sesion = sesionBusinessService.iniciarSesion(userId, request.getEventoId());

        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(
                applicationName,
                "Sesión iniciada exitosamente",
                sesion.getId().toString()
            ))
            .body(sesion);
    }

    /**
     * {@code GET  /compra/estado} : Obtiene el estado actual de la sesión del usuario.
     * Consulta primero Redis, si no existe intenta rehidratar desde PostgreSQL.
     * @return ResponseEntity con el SesionDTO o 404 si no existe
     */
    @GetMapping("/estado")
    public ResponseEntity<SesionDTO> obtenerEstadoSesion() {
        LOG.debug("REST request to obtener estado de sesión");

        Long userId = SecurityUtils.getCurrentUserId()
            .orElseThrow(() -> new BadRequestAlertException(
                "Usuario no autenticado",
                ENTITY_NAME,
                "notauthenticated"
            ));
        SesionDTO sesion = sesionBusinessService.obtenerEstadoSesion(userId);

        if (sesion == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(sesion);
    }

    /**
     * {@code POST  /compra/actividad} : Actualiza la última actividad de la sesión (keep-alive).
     *
     * Extiende el tiempo de expiración en Redis.
     *
     * @return ResponseEntity vacío con código 200
     */
    @PostMapping("/actividad")
    public ResponseEntity<Void> actualizarActividad() {
        LOG.debug("REST request to actualizar actividad de sesión");

        Long userId = SecurityUtils.getCurrentUserId()
            .orElseThrow(() -> new BadRequestAlertException(
                "Usuario no autenticado",
                ENTITY_NAME,
                "notauthenticated"
            ));
        sesionBusinessService.actualizarActividad(userId);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(
                applicationName,
                "Actividad actualizada",
                ""
            ))
            .build();
    }

    /**
     * {@code POST  /compra/seleccionar-asientos} : Selecciona y bloquea asientos en el servidor de cátedra.
     *
     * Valida disponibilidad y bloquea los asientos por 5 minutos.
     * Máximo 4 asientos por sesión.
     *
     * @param request el DTO con la lista de asientos a seleccionar
     * @return ResponseEntity con el SesionDTO actualizado
     */
    @PostMapping("/seleccionar-asientos")
    public ResponseEntity<SesionDTO> seleccionarAsientos(@Valid @RequestBody SeleccionarAsientosRequestDTO request) {
        LOG.debug("REST request to seleccionar asientos: {}", request);

        Long userId = SecurityUtils.getCurrentUserId()
            .orElseThrow(() -> new BadRequestAlertException(
                "Usuario no autenticado",
                ENTITY_NAME,
                "notauthenticated"
            ));
        SesionDTO sesion = sesionBusinessService.seleccionarAsientos(userId, request.getAsientos());

        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(
                applicationName,
                "Asientos seleccionados exitosamente",
                sesion.getId().toString()
            ))
            .body(sesion);
    }

    /**
     * {@code POST  /compra/asignar-nombres} : Asigna nombres a los asientos seleccionados.
     *
     * Valida que el nombre tenga al menos 3 caracteres.
     * Debe haber un nombre por cada asiento seleccionado.
     *
     * @param request el DTO con el Map de nombres (key: "fila-columna", value: "Nombre Completo")
     * @return ResponseEntity con el SesionDTO actualizado
     */
    @PostMapping("/asignar-nombres")
    public ResponseEntity<SesionDTO> asignarNombres(@Valid @RequestBody AsignarNombresRequestDTO request) {
        LOG.debug("REST request to asignar nombres: {}", request);

        Long userId = SecurityUtils.getCurrentUserId()
            .orElseThrow(() -> new BadRequestAlertException(
                "Usuario no autenticado",
                ENTITY_NAME,
                "notauthenticated"
            ));
        SesionDTO sesion = sesionBusinessService.asignarNombres(userId, request.getNombres());

        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(
                applicationName,
                "Nombres asignados exitosamente",
                sesion.getId().toString()
            ))
            .body(sesion);
    }

    /**
     * {@code POST  /compra/confirmar} : Confirma la venta de los asientos seleccionados.
     *
     * Realiza la venta en el servidor de cátedra y persiste los AsientoVendido.
     * Valida que:
     * - Haya asientos seleccionados
     * - Todos los asientos tengan nombre asignado
     * - Los bloqueos sigan vigentes (re-bloquea si expiraron)
     *
     * @return ResponseEntity con el VentaDTO creado
     */
    @PostMapping("/confirmar")
    public ResponseEntity<VentaDTO> confirmarVenta() {
        LOG.debug("REST request to confirmar venta");

        Long userId = SecurityUtils.getCurrentUserId()
            .orElseThrow(() -> new BadRequestAlertException(
                "Usuario no autenticado",
                ENTITY_NAME,
                "notauthenticated"
            ));
        VentaDTO venta = sesionBusinessService.confirmarVenta(userId);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(
                applicationName,
                "Venta confirmada exitosamente",
                venta.getId().toString()
            ))
            .body(venta);
    }

    /**
     * {@code DELETE  /compra/cancelar} : Cancela la sesión activa del usuario.
     * Marca la sesión como COMPLETADO, elimina asientos seleccionados y limpia Redis.
     * @return ResponseEntity vacío con código 204
     */
    @DeleteMapping("/cancelar")
    public ResponseEntity<Void> cancelarSesion() {
        LOG.debug("REST request to cancelar sesión");

        Long userId = SecurityUtils.getCurrentUserId()
            .orElseThrow(() -> new BadRequestAlertException(
                "Usuario no autenticado",
                ENTITY_NAME,
                "notauthenticated"
            ));
        sesionBusinessService.cancelarSesion(userId);

        return ResponseEntity.noContent()
            .headers(HeaderUtil.createAlert(
                applicationName,
                "Sesión cancelada",
                ""
            ))
            .build();
    }
}
