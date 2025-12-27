package com.evento.proxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Proxy.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();

    private final Redis redis = new Redis();

    private final Backend backend = new Backend();

    // jhipster-needle-application-properties-property

    public Liquibase getLiquibase() {
        return liquibase;
    }

    public Redis getRedis() {
        return redis;
    }

    public Backend getBackend() {
        return backend;
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

    public static class Redis {

        private final Catedra catedra = new Catedra();

        public Catedra getCatedra() {
            return catedra;
        }

        public static class Catedra {

            private String host = "192.168.194.250";
            private int port = 6379;
            private int timeout = 2000;

            public String getHost() {
                return host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            public int getPort() {
                return port;
            }

            public void setPort(int port) {
                this.port = port;
            }

            public int getTimeout() {
                return timeout;
            }

            public void setTimeout(int timeout) {
                this.timeout = timeout;
            }
        }
    }

    public static class Backend {
        private String baseUrl = "http://localhost:8081";
        private int timeout = 5000;
        private String serviceUsername = "proxy-service";
        private String servicePassword = "user";
        public String getBaseUrl() {
            return baseUrl;
        }
        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
        public int getTimeout() {
            return timeout;
        }
        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }
        public String getServiceUsername() {
            return serviceUsername;
        }
        public void setServiceUsername(String serviceUsername) {
            this.serviceUsername = serviceUsername;
        }
        public String getServicePassword() {
            return servicePassword;
        }
        public void setServicePassword(String servicePassword) {
            this.servicePassword = servicePassword;
        }
    }
    // jhipster-needle-application-properties-property-class
}
