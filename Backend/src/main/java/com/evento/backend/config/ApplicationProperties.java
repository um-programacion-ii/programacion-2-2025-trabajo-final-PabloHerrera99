package com.evento.backend.config;

import com.evento.backend.domain.Sesion;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Final Programacion.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private final Liquibase liquibase = new Liquibase();
    private final Proxy proxy = new Proxy();
    private final Sesion sesion = new Sesion();

    // jhipster-needle-application-properties-property
    public Liquibase getLiquibase() {
        return liquibase;
    }
    public Proxy getProxy() {
        return proxy;
    }
    public Sesion getSesion() {
        return sesion;
    }

    // jhipster-needle-application-properties-property-getter
    public static class Liquibase {
        private Boolean asyncStart = true;
        public Boolean getAsyncStart() {
            return asyncStart;
        }
        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }
    /**
     * Configuración de conexión con el servicio Proxy
     */
    public static class Proxy {

        private String baseUrl = "http://localhost:8082";
        private Integer timeout = 5000;
        public String getBaseUrl() {
            return baseUrl;
        }
        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
        public Integer getTimeout() {
            return timeout;
        }
        public void setTimeout(Integer timeout) {
            this.timeout = timeout;
        }
    }

    /**
     * Configuración de gestión de sesiones en Redis
     */
    public static class Sesion {
        private Integer ttlMinutos = 30;
        private String keyPrefix = "sesion:usuario:";
        public Integer getTtlMinutos() {
            return ttlMinutos;
        }
        public void setTtlMinutos(Integer ttlMinutos) {
            this.ttlMinutos = ttlMinutos;
        }
        public String getKeyPrefix() {
            return keyPrefix;
        }
        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }
        /**
         * Convierte el TTL de minutos a segundos para uso directo con Redis.
         */
        public long getTtlSegundos() {
            return ttlMinutos * 60L;
        }
    }
    // jhipster-needle-application-properties-property-class
}
