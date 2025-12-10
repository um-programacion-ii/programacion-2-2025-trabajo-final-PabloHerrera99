package com.evento.backend.config;

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
    // jhipster-needle-application-properties-property
    public Liquibase getLiquibase() {
        return liquibase;
    }
    public Proxy getProxy() {
        return proxy;
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
    // jhipster-needle-application-properties-property-class
}
