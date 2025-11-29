package com.um.trabajofinal.demo.service.asiento;

import com.um.trabajofinal.demo.api.dto.AsientoDto;
import com.um.trabajofinal.demo.api.dto.SeatAvailabilityDto;
import com.um.trabajofinal.demo.exception.EventoNotFoundException;
import com.um.trabajofinal.demo.exception.ReservationExpiredException;
import com.um.trabajofinal.demo.exception.SeatNotAvailableException;
import com.um.trabajofinal.demo.persistence.domain.AsientoVenta;
import com.um.trabajofinal.demo.persistence.domain.Evento;
import com.um.trabajofinal.demo.persistence.domain.Venta;
import com.um.trabajofinal.demo.persistence.repository.AsientoVentaRepository;
import com.um.trabajofinal.demo.persistence.repository.EventoRepository;
import com.um.trabajofinal.demo.persistence.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class AsientoVentaServiceImpl implements AsientoVentaService {

    private final AsientoVentaRepository asientoVentaRepository;
    private final EventoRepository eventoRepository;
    private final VentaRepository ventaRepository;
    
    // Cache para reservas temporales (en memoria, se podría usar Redis en producción)
    private final Map<String, ReservaTemporalInfo> reservasTemporales = new ConcurrentHashMap<>();

    public AsientoVentaServiceImpl(AsientoVentaRepository asientoVentaRepository, 
                                  EventoRepository eventoRepository, 
                                  VentaRepository ventaRepository) {
        this.asientoVentaRepository = asientoVentaRepository;
        this.eventoRepository = eventoRepository;
        this.ventaRepository = ventaRepository;
    }

    @Override
    public List<AsientoDto> holdSeats(Long eventoId, List<String> asientos, Long usuarioId) {
        // Validar evento existe
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EventoNotFoundException("Evento con ID " + eventoId + " no encontrado"));

        // Limpiar reservas expiradas primero
        releaseExpiredReservations();

        List<AsientoDto> asientosReservados = new ArrayList<>();
        LocalDateTime expiracion = LocalDateTime.now().plusMinutes(15);

        for (String asientoStr : asientos) {
            String[] parts = parseAsiento(asientoStr);
            String fila = parts[0];
            String numero = parts[1];
            
            // Verificar disponibilidad
            if (isSeatAvailable(eventoId, fila, numero)) {
                String claveReserva = generarClaveReserva(eventoId, fila, numero);
                
                // Crear reserva temporal
                ReservaTemporalInfo reserva = new ReservaTemporalInfo(
                    usuarioId, expiracion, eventoId, fila, numero
                );
                reservasTemporales.put(claveReserva, reserva);
                
                // Crear DTO
                AsientoDto asientoDto = AsientoDto.builder()
                        .fila(fila)
                        .numero(numero)
                        .precio(evento.getPrecioBase())
                        .moneda("ARS")
                        .ocupado(false)
                        .reservadoTemporalmente(true)
                        .tiempoExpiracionReserva(expiracion)
                        .build();
                
                asientosReservados.add(asientoDto);
            } else {
                throw new SeatNotAvailableException("Asiento " + asientoStr + " no está disponible");
            }
        }

        return asientosReservados;
    }

    @Override
    public void releaseSeats(Long eventoId, List<String> asientos) {
        for (String asientoStr : asientos) {
            String[] parts = parseAsiento(asientoStr);
            String claveReserva = generarClaveReserva(eventoId, parts[0], parts[1]);
            reservasTemporales.remove(claveReserva);
        }
    }

    @Override
    public List<AsientoDto> finalizeSeats(Long ventaId, List<String> asientos) {
        // Obtener venta
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        List<AsientoDto> asientosFinalizados = new ArrayList<>();

        for (String asientoStr : asientos) {
            String[] parts = parseAsiento(asientoStr);
            String fila = parts[0];
            String numero = parts[1];
            String claveReserva = generarClaveReserva(venta.getEvento().getId(), fila, numero);
            
            // Verificar que el asiento esté reservado
            ReservaTemporalInfo reserva = reservasTemporales.get(claveReserva);
            if (reserva == null || reserva.getExpiracion().isBefore(LocalDateTime.now())) {
                throw new ReservationExpiredException("Asiento " + asientoStr + " no está reservado o la reserva expiró");
            }
            
            // Crear AsientoVenta permanente
            AsientoVenta asientoVenta = AsientoVenta.builder()
                    .venta(venta)
                    .fila(fila)
                    .columna(numero)
                    .nombrePersona("") // Se puede agregar después
                    .build();
            
            asientoVenta = asientoVentaRepository.save(asientoVenta);
            
            // Remover de reservas temporales
            reservasTemporales.remove(claveReserva);
            
            // Crear DTO
            AsientoDto asientoDto = mapAsientoToDto(asientoVenta, venta.getEvento().getPrecioBase());
            asientosFinalizados.add(asientoDto);
        }

        return asientosFinalizados;
    }

    @Override
    @Transactional(readOnly = true)
    public SeatAvailabilityDto getAvailableSeats(Long eventoId) {
        // Obtener evento
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EventoNotFoundException("Evento con ID " + eventoId + " no encontrado"));

        // Limpiar reservas expiradas
        releaseExpiredReservations();

        // Obtener asientos ocupados permanentemente
        List<AsientoVenta> asientosVendidos = asientoVentaRepository.findByVentaEventoId(eventoId);
        
        // Para este ejemplo, asumimos un layout fijo de asientos (10 filas, 10 columnas)
        List<AsientoDto> todosAsientos = new ArrayList<>();
        int ocupados = 0;
        int reservados = 0;
        int disponibles = 0;

        for (char fila = 'A'; fila <= 'J'; fila++) {
            for (int numero = 1; numero <= 10; numero++) {
                String filaStr = String.valueOf(fila);
                String numeroStr = String.valueOf(numero);
                
                AsientoDto asiento = AsientoDto.builder()
                        .fila(filaStr)
                        .numero(numeroStr)
                        .precio(evento.getPrecioBase())
                        .moneda("ARS")
                        .build();

                // Verificar si está vendido
                boolean vendido = asientosVendidos.stream()
                        .anyMatch(av -> av.getFila().equals(filaStr) && av.getColumna().equals(numeroStr));
                
                if (vendido) {
                    asiento.setOcupado(true);
                    asiento.setReservadoTemporalmente(false);
                    ocupados++;
                } else {
                    // Verificar si está reservado temporalmente
                    String claveReserva = generarClaveReserva(eventoId, filaStr, numeroStr);
                    ReservaTemporalInfo reserva = reservasTemporales.get(claveReserva);
                    
                    if (reserva != null && reserva.getExpiracion().isAfter(LocalDateTime.now())) {
                        asiento.setOcupado(false);
                        asiento.setReservadoTemporalmente(true);
                        asiento.setTiempoExpiracionReserva(reserva.getExpiracion());
                        reservados++;
                    } else {
                        asiento.setOcupado(false);
                        asiento.setReservadoTemporalmente(false);
                        disponibles++;
                    }
                }
                
                todosAsientos.add(asiento);
            }
        }

        return SeatAvailabilityDto.builder()
                .eventoId(eventoId)
                .eventoNombre(evento.getNombre())
                .totalAsientos(todosAsientos.size())
                .asientosDisponibles(disponibles)
                .asientosOcupados(ocupados)
                .asientosReservados(reservados)
                .asientos(todosAsientos)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSeatAvailable(Long eventoId, String fila, String numero) {
        // Verificar si está vendido
        boolean vendido = asientoVentaRepository.existsByVentaEventoIdAndFilaAndColumna(eventoId, fila, numero);
        if (vendido) {
            return false;
        }

        // Verificar si está reservado temporalmente
        String claveReserva = generarClaveReserva(eventoId, fila, numero);
        ReservaTemporalInfo reserva = reservasTemporales.get(claveReserva);
        return reserva == null || reserva.getExpiracion().isBefore(LocalDateTime.now());
    }

    @Override
    public void releaseExpiredReservations() {
        LocalDateTime ahora = LocalDateTime.now();
        reservasTemporales.entrySet().removeIf(entry -> entry.getValue().getExpiracion().isBefore(ahora));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsientoDto> getAsientosByVenta(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        return venta.getAsientos().stream()
                .map(asiento -> mapAsientoToDto(asiento, venta.getEvento().getPrecioBase()))
                .collect(Collectors.toList());
    }

    // Métodos privados auxiliares
    
    private String[] parseAsiento(String asientoStr) {
        // Formato esperado: "A1", "B2", etc.
        if (asientoStr.length() < 2) {
            throw new RuntimeException("Formato de asiento inválido: " + asientoStr);
        }
        String fila = asientoStr.substring(0, 1);
        String numero = asientoStr.substring(1);
        return new String[]{fila, numero};
    }

    private String generarClaveReserva(Long eventoId, String fila, String numero) {
        return eventoId + "_" + fila + "_" + numero;
    }

    private AsientoDto mapAsientoToDto(AsientoVenta asientoVenta, BigDecimal precioBase) {
        return AsientoDto.builder()
                .id(asientoVenta.getId())
                .fila(asientoVenta.getFila())
                .numero(asientoVenta.getColumna())
                .precio(precioBase)
                .moneda("ARS")
                .ocupado(true)
                .reservadoTemporalmente(false)
                .ventaId(asientoVenta.getVenta().getId())
                .build();
    }

    // Clase interna para manejar reservas temporales
    private static class ReservaTemporalInfo {
        private final Long usuarioId;
        private final LocalDateTime expiracion;
        private final Long eventoId;
        private final String fila;
        private final String numero;

        public ReservaTemporalInfo(Long usuarioId, LocalDateTime expiracion, 
                                  Long eventoId, String fila, String numero) {
            this.usuarioId = usuarioId;
            this.expiracion = expiracion;
            this.eventoId = eventoId;
            this.fila = fila;
            this.numero = numero;
        }

        public Long getUsuarioId() { return usuarioId; }
        public LocalDateTime getExpiracion() { return expiracion; }
        public Long getEventoId() { return eventoId; }
        public String getFila() { return fila; }
        public String getNumero() { return numero; }
    }
}