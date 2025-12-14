package com.evento.mobile.di

/**
 * Implementación Android de la configuración del API.
 *
 * En el emulador de Android, la IP 10.0.2.2 es una dirección especial
 * que redirige al localhost (127.0.0.1) de la máquina host.
 *
 * Esto permite que la app corriendo en el emulador se conecte al
 * backend que está corriendo en tu computadora en el puerto 8081.
 *
 * Tabla de IPs especiales del emulador Android:
 * - 10.0.2.1      → Router/gateway
 * - 10.0.2.2      → Localhost del host (¡la que necesitamos!)
 * - 10.0.2.3      → DNS server
 * - 10.0.2.15     → IP del emulador mismo
 */
actual object ApiConfig {
    actual val BASE_URL: String = "http://10.0.2.2:8081"
}