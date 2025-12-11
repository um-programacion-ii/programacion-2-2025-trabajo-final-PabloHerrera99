package com.evento.backend.service.helper;

import com.evento.backend.domain.Evento;
import com.evento.backend.domain.User;
import com.evento.backend.domain.Venta;
import com.evento.backend.domain.enumeration.EstadoSincronizacion;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Helper para creación de entidades Venta con diferentes estados de sincronización.
 * Centraliza la lógica de construcción según el resultado de la operación con cátedra.
 */
@Service
public class VentaCreationHelper {

    /**
     * Crea venta con estado PENDIENTE.
     * 
     * Usado cuando el servidor de cátedra no responde (timeout, 500, connection refused).
     * La venta queda marcada como pendiente para re-intento posterior.
     * 
     * @param usuario Usuario que realiza la compra
     * @param evento Evento de la venta
     * @param precioTotal Precio total calculado
     * @return Venta con estado PENDIENTE (no exitosa)
     */
    public Venta crearVentaPendiente(User usuario, Evento evento, BigDecimal precioTotal) {
        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setEvento(evento);
        venta.setIdVentaCatedra(null); // Sin ID porque no se confirmó
        venta.setFechaVenta(Instant.now());
        venta.setPrecioTotal(precioTotal);
        venta.setExitosa(false);
        venta.setEstadoSincronizacion(EstadoSincronizacion.PENDIENTE);
        return venta;
    }

    /**
     * Crea venta con estado SINCRONIZADO (exitosa).
     * 
     * Usado cuando cátedra confirma la venta exitosamente (resultado: true).
     * 
     * @param usuario Usuario que realiza la compra
     * @param evento Evento de la venta
     * @param idVentaCatedra ID de venta asignado por servidor de cátedra
     * @param precioTotal Precio total calculado
     * @return Venta con estado SINCRONIZADO (exitosa)
     */
    public Venta crearVentaExitosa(User usuario, Evento evento, Long idVentaCatedra, BigDecimal precioTotal) {
        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setEvento(evento);
        venta.setIdVentaCatedra(idVentaCatedra);
        venta.setFechaVenta(Instant.now());
        venta.setPrecioTotal(precioTotal);
        venta.setExitosa(true);
        venta.setEstadoSincronizacion(EstadoSincronizacion.SINCRONIZADA);
        return venta;
    }

    /**
     * Crea venta con estado ERROR (rechazada).
     * 
     * Usado cuando cátedra rechaza la venta por razones de negocio:
     * - Asientos ya vendidos
     * - Evento no disponible
     * - Validación de negocio fallida
     * 
     * @param usuario Usuario que realiza la compra
     * @param evento Evento de la venta
     * @param precioTotal Precio total calculado
     * @return Venta con estado ERROR (no exitosa)
     */
    public Venta crearVentaRechazada(User usuario, Evento evento, BigDecimal precioTotal) {
        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setEvento(evento);
        venta.setIdVentaCatedra(null); // Sin ID porque fue rechazada
        venta.setFechaVenta(Instant.now());
        venta.setPrecioTotal(precioTotal);
        venta.setExitosa(false);
        venta.setEstadoSincronizacion(EstadoSincronizacion.ERROR);
        return venta;
    }
}
