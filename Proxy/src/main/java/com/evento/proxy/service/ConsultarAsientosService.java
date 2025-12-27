package com.evento.proxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

/**
 * Service para cosultar los asientos libres de la catesra.
 */
@Service
public class ConsultarAsientosService {

    private static final Logger log = LoggerFactory.getLogger(ConsultarAsientosService.class);
    private static final String REDIS_KEY_PREFIX = "evento_";

    private final JedisPool jedisPool;

    public ConsultarAsientosService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }


    public String obtenerAsientos(Long eventoId) {
        String key = REDIS_KEY_PREFIX + eventoId;

        try (Jedis jedis = jedisPool.getResource()) {
            log.debug("Consulting Redis for key: {}", key);
            String data = jedis.get(key);

            if (data != null) {
                log.debug("Found seat data for event {}", eventoId);
            } else {
                log.debug("No seat data found for event {}", eventoId);
            }

            return data;
        } catch (JedisException e) {
            log.error("Error connecting to Redis for event {}: {}", eventoId, e.getMessage());
            throw new RuntimeException("Error accessing Redis server", e);
        }
    }
}
