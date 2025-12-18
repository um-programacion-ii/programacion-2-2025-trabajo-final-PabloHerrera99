package com.evento.backend.service;

import com.evento.backend.config.ApplicationProperties;
import com.evento.backend.service.dto.RedisResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;

/**
 * Servicio cliente HTTP para comunicarse con el Proxy
 *
 * El Proxy actúa como intermediario entre el Backend y Redis de cátedra
 * Este servicio consume el endpoint del Proxy para obtener datos de asientos
 */
@Service
public class ProxyClientService {
    private static final Logger log = LoggerFactory.getLogger(ProxyClientService.class);
    private final ApplicationProperties applicationProperties;
    private final RestTemplate restTemplate;
    public ProxyClientService(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Obtiene el token JWT
     *
     * @return Token JWT del usuario autenticado
     * @throws RuntimeException si no hay token JWT en el contexto
     */
    private String obtenerTokenActual() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getTokenValue();
        }
        throw new RuntimeException("No se pudo obtener el token JWT del contexto de seguridad");
    }

    /**
     * Consulta asientos bloqueados/vendidos desde el Proxy
     *
     * El Proxy consulta Redis de cátedra con la key: "evento_{eventoIdCatedra}"
     *
     * @param eventoIdCatedra - ID del evento en servidor de cátedra (NO el ID local)
     * @return Optional con datos de Redis, o empty si no hay datos o hay error
     */
    public Optional<RedisResponseDTO> consultarAsientosDesdeProxy(Long eventoIdCatedra) {
        String proxyBaseUrl = applicationProperties.getProxy().getBaseUrl();
        String url = proxyBaseUrl + "/api/proxy/asientos/" + eventoIdCatedra;
        log.debug("Consultando asientos desde Proxy: {}", url);
        try {
            // Obtener token JWT del usuario actual
            String token = obtenerTokenActual();

            // Crear headers con Authorization Bearer
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Hacer petición HTTP con autenticación
            ResponseEntity<RedisResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                RedisResponseDTO.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                RedisResponseDTO data = response.getBody();
                log.debug("Respuesta de Proxy recibida: {} asientos",
                    data.getAsientos() != null ? data.getAsientos().size() : 0);
                return Optional.of(data);
            }
            log.debug("Respuesta vacía desde Proxy para evento {}", eventoIdCatedra);
            return Optional.empty();
        } catch (HttpClientErrorException.NotFound e) {
            // 404 = No hay datos en Redis para este evento (todos los asientos disponibles)
            log.debug("Evento {} no tiene asientos bloqueados/vendidos en Redis (404)",
                eventoIdCatedra);
            return Optional.empty();
        } catch (HttpClientErrorException.Unauthorized e) {
            // 401 = Problema de autenticación con Proxy (si lo tiene configurado)
            log.warn("Error de autenticación al consultar Proxy: {}", e.getMessage());
            throw new RuntimeException("No autorizado para consultar Proxy", e);
        } catch (ResourceAccessException e) {
            // Error de conectividad (Proxy caído, timeout, etc.)
            log.error("Proxy no disponible o timeout: {}", e.getMessage());
            throw new RuntimeException("Proxy no disponible", e);
        } catch (Exception e) {
            // Error inesperado
            log.error("Error inesperado consultando Proxy: {}", e.getMessage(), e);
            throw new RuntimeException("Error comunicándose con Proxy", e);
        }
    }
    /**
     * Verifica si el Proxy está disponible
     *
     * @return true si el Proxy responde, false en caso contrario
     */
    public boolean isProxyAvailable() {
        String proxyBaseUrl = applicationProperties.getProxy().getBaseUrl();
        String healthUrl = proxyBaseUrl + "/management/health";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                healthUrl,
                String.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.debug("Proxy no disponible: {}", e.getMessage());
            return false;
        }
    }
}
