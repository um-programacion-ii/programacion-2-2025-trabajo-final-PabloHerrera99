package com.evento.backend.service;

import com.evento.backend.config.CatedraProperties;
import com.evento.backend.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.util.List;
/**
 * Servicio cliente HTTP para comunicarse con el servidor de cátedra.
 *
 * Responsabilidades:
 * - Bloquear asientos temporalmente (5 minutos)
 * - Realizar ventas de asientos
 * - Manejo de autenticación JWT
 * - Manejo de errores de comunicación
 */
@Service
public class CatedraClientService {
    private static final Logger LOG = LoggerFactory.getLogger(CatedraClientService.class);
    private final CatedraProperties catedraProperties;
    private final CatedraAuthService catedraAuthService;
    private final RestTemplate restTemplate;
    public CatedraClientService(
        CatedraProperties catedraProperties,
        CatedraAuthService catedraAuthService
    ) {
        this.catedraProperties = catedraProperties;
        this.catedraAuthService = catedraAuthService;
        this.restTemplate = new RestTemplate();
    }
    /**
     * Bloquea asientos temporalmente en el servidor de cátedra.
     * Duración del bloqueo: 5 minutos
     *
     * @param eventoIdCatedra ID del evento en servidor de cátedra (NO el ID local)
     * @param asientos Lista de asientos a bloquear
     * @return Response con resultado del bloqueo
     * @throws RuntimeException si hay error de comunicación o autenticación
     */
    public BloquearAsientosResponseDTO bloquearAsientos(
        Long eventoIdCatedra,
        List<AsientoSimpleDTO> asientos
    ) {
        LOG.info("Bloqueando {} asientos en evento cátedra {}", asientos.size(), eventoIdCatedra);
        try {
            // 1. Construir URL
            String baseUrl = catedraProperties.getBaseUrl();
            String url = baseUrl + "/api/endpoints/v1/bloquear-asientos";
            LOG.debug("URL bloqueo: {}", url);
            // 2. Obtener token JWT
            String token = catedraAuthService.getValidToken();
            // 3. Construir request DTO
            BloquearAsientosRequestDTO request = new BloquearAsientosRequestDTO(
                eventoIdCatedra,
                asientos
            );
            // 4. Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);
            // 5. Crear entity con headers y body
            HttpEntity<BloquearAsientosRequestDTO> entity = new HttpEntity<>(request, headers);
            // 6. Hacer POST request
            ResponseEntity<BloquearAsientosResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                BloquearAsientosResponseDTO.class
            );
            // 7. Validar response
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                BloquearAsientosResponseDTO responseBody = response.getBody();

                if (Boolean.TRUE.equals(responseBody.getResultado())) {
                    LOG.info("✓ Asientos bloqueados exitosamente en cátedra - Evento: {}",
                        eventoIdCatedra);
                } else {
                    LOG.warn("✗ Bloqueo rechazado por cátedra: {}",
                        responseBody.getDescripcion());
                }

                return responseBody;
            } else {
                LOG.error("Response vacía o status code inválido: {}", response.getStatusCode());
                throw new RuntimeException("Response inválida desde servidor cátedra");
            }
        } catch (HttpClientErrorException.Unauthorized e) {
            LOG.error("Token JWT inválido o expirado: {}", e.getMessage());
            // Intentar renovar token
            catedraAuthService.renewToken();
            throw new RuntimeException("No autorizado para bloquear asientos en cátedra. Token renovado, reintente.", e);
        } catch (HttpClientErrorException.BadRequest e) {
            LOG.error("Request inválido al bloquear asientos: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Request de bloqueo inválido: " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            LOG.error("Error interno del servidor cátedra: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Error en servidor cátedra", e);
        } catch (ResourceAccessException e) {
            LOG.error("Servidor cátedra no disponible o timeout: {}", e.getMessage());
            throw new RuntimeException("Servidor cátedra no disponible", e);
        } catch (Exception e) {
            LOG.error("Error inesperado al bloquear asientos: {}", e.getMessage(), e);
            throw new RuntimeException("Error al comunicarse con servidor cátedra", e);
        }
    }
    /**
     * Realiza una venta de asientos en el servidor de cátedra.
     * Los asientos deben estar previamente bloqueados.
     *
     * @param request DTO con datos de la venta
     * @return Response con resultado de la venta (incluye ventaId si exitosa)
     * @throws RuntimeException si hay error de comunicación o autenticación
     */
    public RealizarVentaResponseDTO realizarVenta(RealizarVentaRequestDTO request) {
        LOG.info("Realizando venta de {} asientos en evento cátedra {}",
            request.getAsientos().size(), request.getEventoId());
        try {
            // 1. Construir URL
            String baseUrl = catedraProperties.getBaseUrl();
            String url = baseUrl + "/api/endpoints/v1/realizar-venta";
            LOG.debug("URL venta: {}", url);
            // 2. Obtener token JWT
            String token = catedraAuthService.getValidToken();
            // 3. Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);
            // 4. Crear entity con headers y body
            HttpEntity<RealizarVentaRequestDTO> entity = new HttpEntity<>(request, headers);
            // 5. Hacer POST request
            ResponseEntity<RealizarVentaResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                RealizarVentaResponseDTO.class
            );
            // 6. Validar response
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                RealizarVentaResponseDTO responseBody = response.getBody();

                if (Boolean.TRUE.equals(responseBody.getResultado())) {
                    LOG.info("✓ Venta realizada exitosamente en cátedra - VentaId: {}, Evento: {}",
                        responseBody.getVentaId(), responseBody.getEventoId());
                } else {
                    LOG.warn("✗ Venta rechazada por cátedra: {}",
                        responseBody.getDescripcion());
                }

                return responseBody;
            } else {
                LOG.error("Response vacía o status code inválido: {}", response.getStatusCode());
                throw new RuntimeException("Response inválida desde servidor cátedra");
            }
        } catch (HttpClientErrorException.Unauthorized e) {
            LOG.error("Token JWT inválido o expirado: {}", e.getMessage());
            // Intentar renovar token
            catedraAuthService.renewToken();
            throw new RuntimeException("No autorizado para realizar ventas en cátedra. Token renovado, reintente.", e);
        } catch (HttpClientErrorException.BadRequest e) {
            LOG.error("Request inválido al realizar venta: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Request de venta inválido: " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            LOG.error("Error interno del servidor cátedra: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Error en servidor cátedra", e);
        } catch (ResourceAccessException e) {
            LOG.error("Servidor cátedra no disponible o timeout: {}", e.getMessage());
            throw new RuntimeException("Servidor cátedra no disponible", e);
        } catch (Exception e) {
            LOG.error("Error inesperado al realizar venta: {}", e.getMessage(), e);
            throw new RuntimeException("Error al comunicarse con servidor cátedra", e);
        }
    }
    /**
     * Verifica si el servidor de cátedra está disponible.
     *
     * @return true si el servidor responde, false en caso contrario
     */
    public boolean isServerAvailable() {
        try {
            String baseUrl = catedraProperties.getBaseUrl();
            // Intentar ping a endpoint base (ajustar según disponibilidad)
            restTemplate.getForEntity(baseUrl + "/management/health", String.class);
            return true;
        } catch (Exception e) {
            LOG.debug("Servidor cátedra no disponible: {}", e.getMessage());
            return false;
        }
    }
}
