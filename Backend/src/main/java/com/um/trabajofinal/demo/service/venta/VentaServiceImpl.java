package com.um.trabajofinal.demo.service.venta;

import com.um.trabajofinal.demo.api.dto.AsientoDto;
import com.um.trabajofinal.demo.api.dto.VentaDto;
import com.um.trabajofinal.demo.exception.EventoNotFoundException;
import com.um.trabajofinal.demo.persistence.domain.AsientoVenta;
import com.um.trabajofinal.demo.persistence.domain.Evento;
import com.um.trabajofinal.demo.persistence.domain.Usuario;
import com.um.trabajofinal.demo.persistence.domain.Venta;
import com.um.trabajofinal.demo.persistence.domain.enums.EstadoVenta;
import com.um.trabajofinal.demo.persistence.repository.EventoRepository;
import com.um.trabajofinal.demo.persistence.repository.UsuarioRepository;
import com.um.trabajofinal.demo.persistence.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;

    public VentaServiceImpl(VentaRepository ventaRepository, 
                           UsuarioRepository usuarioRepository, 
                           EventoRepository eventoRepository) {
        this.ventaRepository = ventaRepository;
        this.usuarioRepository = usuarioRepository;
        this.eventoRepository = eventoRepository;
    }

    @Override
    public VentaDto createVenta(VentaDto ventaDto) {
        // Validar usuario
        Usuario usuario = usuarioRepository.findById(ventaDto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Validar evento
        Evento evento = eventoRepository.findById(ventaDto.getEventoId())
                .orElseThrow(() -> new EventoNotFoundException("Evento con ID " + ventaDto.getEventoId() + " no encontrado"));

        // Crear venta
        Venta venta = Venta.builder()
                .usuario(usuario)
                .evento(evento)
                .precioTotal(ventaDto.getMontoTotal())
                .estado(ventaDto.getEstado() != null ? ventaDto.getEstado() : EstadoVenta.PENDIENTE)
                .fechaHora(LocalDateTime.now())
                .build();

        // Guardar venta
        venta = ventaRepository.save(venta);
        
        return mapToDto(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VentaDto> findVentaById(Long id) {
        return ventaRepository.findById(id)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaDto> findVentasByUsuario(Long usuarioId) {
        return ventaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public VentaDto updateVentaEstado(Long id, EstadoVenta estado) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        
        venta.setEstado(estado);
        venta = ventaRepository.save(venta);
        
        return mapToDto(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VentaDto> findVentaByExternalId(String externalId) {
        // Nota: La entidad Venta no tiene externalId, esta implementación es para futuro
        // Por ahora retornamos Optional.empty()
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaDto> findAllVentas() {
        return ventaRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Mapear entidad Venta a VentaDto
     */
    private VentaDto mapToDto(Venta venta) {
        // Mapear asientos
        List<AsientoDto> asientos = venta.getAsientos().stream()
                .map(this::mapAsientoToDto)
                .collect(Collectors.toList());

        return VentaDto.builder()
                .id(venta.getId())
                .externalId(generateExternalId(venta.getId())) // Generar ID externo
                .eventoId(venta.getEvento().getId())
                .eventoNombre(venta.getEvento().getNombre())
                .usuarioId(venta.getUsuario().getId())
                .usuarioEmail(venta.getUsuario().getUsername())
                .montoTotal(venta.getPrecioTotal())
                .moneda("ARS") // Por defecto ARS
                .estado(venta.getEstado())
                .fechaCreacion(venta.getFechaHora())
                .fechaModificacion(venta.getFechaHora())
                .asientos(asientos)
                .build();
    }

    /**
     * Mapear AsientoVenta a AsientoDto
     */
    private AsientoDto mapAsientoToDto(AsientoVenta asientoVenta) {
        return AsientoDto.builder()
                .id(asientoVenta.getId())
                .fila(asientoVenta.getFila())
                .numero(asientoVenta.getColumna())
                .precio(BigDecimal.ZERO) // Por defecto, se debe calcular
                .moneda("ARS")
                .ocupado(true) // Si está en venta, está ocupado
                .reservadoTemporalmente(false)
                .ventaId(asientoVenta.getVenta().getId())
                .build();
    }

    /**
     * Generar ID externo para la venta
     */
    private String generateExternalId(Long ventaId) {
        return "VTA-" + ventaId + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}