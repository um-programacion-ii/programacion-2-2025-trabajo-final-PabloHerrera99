package com.evento.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AsientoSeleccionadoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static AsientoSeleccionado getAsientoSeleccionadoSample1() {
        return new AsientoSeleccionado().id(1L).fila(1).columna(1).nombrePersona("nombrePersona1");
    }

    public static AsientoSeleccionado getAsientoSeleccionadoSample2() {
        return new AsientoSeleccionado().id(2L).fila(2).columna(2).nombrePersona("nombrePersona2");
    }

    public static AsientoSeleccionado getAsientoSeleccionadoRandomSampleGenerator() {
        return new AsientoSeleccionado()
            .id(longCount.incrementAndGet())
            .fila(intCount.incrementAndGet())
            .columna(intCount.incrementAndGet())
            .nombrePersona(UUID.randomUUID().toString());
    }
}
