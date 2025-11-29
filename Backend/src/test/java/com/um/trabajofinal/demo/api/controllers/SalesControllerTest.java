package com.um.trabajofinal.demo.api.controllers;

import com.um.trabajofinal.demo.api.dto.*;
import com.um.trabajofinal.demo.service.asiento.AsientoVentaService;
import com.um.trabajofinal.demo.service.venta.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

public class
SalesControllerTest {

    private AsientoVentaService asientoVentaService;
    private VentaService ventaService;
    private SalesController salesController;

    @BeforeEach
    void setup() {
        asientoVentaService = Mockito.mock(AsientoVentaService.class);
        ventaService = Mockito.mock(VentaService.class);
        salesController = new SalesController(asientoVentaService, ventaService);
    }

    @Test
    void holdSeats_callsServiceAndReturnsDtos() {
        List<String> seats = List.of("A1");
        HoldSeatsRequest req = HoldSeatsRequest.builder()
                .usuarioId(1L)
                .asientos(seats)
                .build();

        AsientoDto dto = AsientoDto.builder()
                .fila("A").numero("1").build();
        given(asientoVentaService.holdSeats(anyLong(), eq(seats), eq(1L))).willReturn(List.of(dto));

        ResponseEntity<ApiResponse<List<AsientoDto>>> response = salesController.holdSeats(100L, req);
        
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        assertEquals("A", response.getBody().getData().get(0).getFila());
        assertEquals("1", response.getBody().getData().get(0).getNumero());
    }

    @Test
    void getSeatAvailability_callsServiceAndReturnsDto() {
        SeatAvailabilityDto avail = SeatAvailabilityDto.builder().eventoId(100L).build();
        given(asientoVentaService.getAvailableSeats(100L)).willReturn(avail);

        ResponseEntity<ApiResponse<SeatAvailabilityDto>> response = salesController.getSeatAvailability(100L);
        
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(100L, response.getBody().getData().getEventoId());
    }

    @Test
    void finalizeSeats_callsServiceAndReturnsDtos() {
        List<String> seats = List.of("A1");
        FinalizeSeatsRequest req = FinalizeSeatsRequest.builder()
                .ventaId(200L)
                .asientos(seats)
                .build();
        AsientoDto dto = AsientoDto.builder().fila("A").numero("1").build();
        given(asientoVentaService.finalizeSeats(200L, seats)).willReturn(List.of(dto));

        ResponseEntity<ApiResponse<List<AsientoDto>>> response = salesController.finalizeSeats(200L, req);
        
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        assertEquals("A", response.getBody().getData().get(0).getFila());
        assertEquals("1", response.getBody().getData().get(0).getNumero());
    }
}
