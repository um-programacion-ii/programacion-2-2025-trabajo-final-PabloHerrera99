package com.evento.backend.service;

import com.evento.backend.service.dto.SesionRedisDTO;
import com.evento.backend.config.ApplicationProperties;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
/**
 * Servicio para gestionar sesiones de usuario en Redis local (volátil).
 *
 * Las sesiones se almacenan con TTL de 30 minutos y expiran automáticamente.
 * Pattern de keys: "sesion:usuario:{userId}"
 *
 * Responsabilidades:
 * - CRUD de sesiones en Redis
 * - Gestión de TTL (Time To Live)
 * - Extensión de expiración (keep-alive)
 */
@Service
public class SesionRedisService {

    private static final Logger LOG = LoggerFactory.getLogger(SesionRedisService.class);

    private final RedissonClient redissonClient;
    private final ApplicationProperties.Sesion sesionProperties;

    public SesionRedisService(
        RedissonClient redissonClient,
        ApplicationProperties applicationProperties
    ) {
        this.redissonClient = redissonClient;
        this.sesionProperties = applicationProperties.getSesion();
    }

    /**
     * Genera la key de Redis para un usuario
     *
     * @param userId Username del usuario
     * @return Key en formato "sesion:usuario:{userId}"
     */
    private String generarKey(String userId) {
        return sesionProperties.getKeyPrefix() + userId;
    }

    /**
     * Obtiene la sesión activa de un usuario desde Redis.
     *
     * @param userId Username del usuario
     * @return Optional con SesionRedisDTO si existe, empty si no existe o expiró
     */
    public Optional<SesionRedisDTO> obtenerSesion(String userId) {
        String key = generarKey(userId);
        LOG.debug("Obteniendo sesión desde Redis: key={}", key);

        try {
            RBucket<SesionRedisDTO> bucket = redissonClient.getBucket(key);
            SesionRedisDTO sesion = bucket.get();

            if (sesion != null) {
                LOG.debug("Sesión encontrada para usuario {}: eventoId={}, estado={}",
                    userId, sesion.getEventoId(), sesion.getEstado());
                return Optional.of(sesion);
            } else {
                LOG.debug("No existe sesión para usuario {}", userId);
                return Optional.empty();
            }
        } catch (Exception e) {
            LOG.error("Error al obtener sesión de Redis para usuario {}: {}", userId, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Guarda o actualiza una sesión en Redis con TTL.
     *
     * @param userId Username del usuario
     * @param sesion Datos de la sesión a guardar
     */
    public void guardarSesion(String userId, SesionRedisDTO sesion) {
        String key = generarKey(userId);
        long ttlSegundos = sesionProperties.getTtlSegundos();

        LOG.debug("Guardando sesión en Redis: key={}, ttl={}s", key, ttlSegundos);

        try {
            // Actualizar campos de control
            sesion.setUserId(userId);
            sesion.setUltimaActividad(Instant.now());
            sesion.setExpiracion(Instant.now().plusSeconds(ttlSegundos));

            RBucket<SesionRedisDTO> bucket = redissonClient.getBucket(key);
            bucket.set(sesion, ttlSegundos, TimeUnit.SECONDS);

            LOG.info("Sesión guardada para usuario {}: eventoId={}, estado={}, expira en {}s",
                userId, sesion.getEventoId(), sesion.getEstado(), ttlSegundos);
        } catch (Exception e) {
            LOG.error("Error al guardar sesión en Redis para usuario {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error al guardar sesión en Redis", e);
        }
    }

    /**
     * Actualiza el timestamp de última actividad y extiende el TTL a 30 minutos.
     *
     * @param userId Username del usuario
     * @return true si se actualizó, false si no existe sesión
     */
    public boolean actualizarActividad(String userId) {
        Optional<SesionRedisDTO> sesionOpt = obtenerSesion(userId);

        if (sesionOpt.isEmpty()) {
            LOG.debug("No se puede actualizar actividad: no existe sesión para usuario {}", userId);
            return false;
        }

        SesionRedisDTO sesion = sesionOpt.get();
        LOG.debug("Actualizando actividad para usuario {}", userId);

        // guardarSesion() ya actualiza ultimaActividad y expiracion, y resetea TTL
        guardarSesion(userId, sesion);

        return true;
    }

    /**
     * Elimina la sesión de un usuario de Redis.
     *
     * @param userId Username del usuario
     * @return true si se eliminó, false si no existía
     */
    public boolean eliminarSesion(String userId) {
        String key = generarKey(userId);
        LOG.debug("Eliminando sesión de Redis: key={}", key);

        try {
            RBucket<SesionRedisDTO> bucket = redissonClient.getBucket(key);
            boolean deleted = bucket.delete();

            if (deleted) {
                LOG.info("✓ Sesión eliminada para usuario {}", userId);
            } else {
                LOG.debug("No había sesión que eliminar para usuario {}", userId);
            }

            return deleted;
        } catch (Exception e) {
            LOG.error("✗ Error al eliminar sesión de Redis para usuario {}: {}", userId, e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si existe una sesión activa para un usuario.
     *
     * @param userId Username del usuario
     * @return true si existe sesión, false si no existe o expiró
     */
    public boolean existeSesion(String userId) {
        String key = generarKey(userId);

        try {
            RBucket<SesionRedisDTO> bucket = redissonClient.getBucket(key);
            boolean existe = bucket.isExists();

            LOG.debug("Verificando existencia de sesión para usuario {}: {}", userId, existe);
            return existe;
        } catch (Exception e) {
            LOG.error("Error al verificar existencia de sesión para usuario {}: {}", userId, e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene el tiempo restante de vida de una sesión en segundos.
     *
     * @param userId Username del usuario
     * @return Tiempo restante en segundos, o 0 si no existe sesión
     */
    public long obtenerTiempoRestante(String userId) {
        String key = generarKey(userId);

        try {
            RBucket<SesionRedisDTO> bucket = redissonClient.getBucket(key);

            if (!bucket.isExists()) {
                LOG.debug("No existe sesión para calcular tiempo restante: usuario={}", userId);
                return 0;
            }

            long ttlMs = bucket.remainTimeToLive();
            long ttlSegundos = ttlMs / 1000;

            LOG.debug("Tiempo restante para sesión de usuario {}: {}s", userId, ttlSegundos);
            return ttlSegundos;
        } catch (Exception e) {
            LOG.error("Error al obtener tiempo restante de sesión para usuario {}: {}", userId, e.getMessage());
            return 0;
        }
    }
}
