package com.evento.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AsientoVendidoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static AsientoVendido getAsientoVendidoSample1() {
        return new AsientoVendido().id(1L).fila(1).columna(1).nombrePersona("nombrePersona1").estado("estado1");
    }

    public static AsientoVendido getAsientoVendidoSample2() {
        return new AsientoVendido().id(2L).fila(2).columna(2).nombrePersona("nombrePersona2").estado("estado2");
    }

    public static AsientoVendido getAsientoVendidoRandomSampleGenerator() {
        return new AsientoVendido()
            .id(longCount.incrementAndGet())
            .fila(intCount.incrementAndGet())
            .columna(intCount.incrementAndGet())
            .nombrePersona(UUID.randomUUID().toString())
            .estado(UUID.randomUUID().toString());
    }
}
