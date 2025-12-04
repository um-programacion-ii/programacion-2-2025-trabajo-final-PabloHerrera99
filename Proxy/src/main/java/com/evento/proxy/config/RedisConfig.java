package com.evento.proxy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Configuration for Redis connection to Cátedra server.
 */
@Configuration
public class RedisConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    private final ApplicationProperties applicationProperties;

    public RedisConfig(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public JedisPool jedisPool() {
        ApplicationProperties.Redis.Catedra catedraConfig = applicationProperties.getRedis().getCatedra();
        
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);
        poolConfig.setTestOnBorrow(true);
        
        log.info("Configuring Redis connection to Cátedra: {}:{}", 
            catedraConfig.getHost(), catedraConfig.getPort());
        
        return new JedisPool(
            poolConfig,
            catedraConfig.getHost(),
            catedraConfig.getPort(),
            catedraConfig.getTimeout()
        );
    }
}
