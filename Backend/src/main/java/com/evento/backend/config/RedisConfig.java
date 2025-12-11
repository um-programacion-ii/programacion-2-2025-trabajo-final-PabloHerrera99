package com.evento.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.codec.JsonJacksonCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Configuración de Redis local para gestión de sesiones de usuario.
 * Utiliza Redisson como cliente Redis
 */
@Configuration
public class RedisConfig {
    private static final Logger LOG = LoggerFactory.getLogger(RedisConfig.class);
    /**
     * Configura el cliente Redisson conectado a Redis local.
     *
     * @return RedissonClient configurado
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        // Configuración single server (Redis local en desarrollo)
        config.useSingleServer()
            .setAddress("redis://localhost:6379")
            .setConnectionPoolSize(10)       // Pool de 10 conexiones
            .setConnectionMinimumIdleSize(2) // Mínimo 2 conexiones idle
            .setTimeout(5000)                // Timeout 5 segundos
            .setRetryAttempts(3)             // 3 reintentos si falla
            .setRetryInterval(1500);         // 1.5 segundos entre reintentos

        // Configurar ObjectMapper con soporte para Java 8 date/time types (Instant, LocalDateTime, etc.)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Codec JSON para serializar/deserializar objetos automáticamente con ObjectMapper configurado
        config.setCodec(new JsonJacksonCodec(objectMapper));

        LOG.info("Configurando RedissonClient para Redis local: localhost:6379");

        return Redisson.create(config);
    }
}
