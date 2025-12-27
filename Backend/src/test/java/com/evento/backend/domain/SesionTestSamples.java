package com.evento.backend.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class SesionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Sesion getSesionSample1() {
        return new Sesion().id(1L);
    }

    public static Sesion getSesionSample2() {
        return new Sesion().id(2L);
    }

    public static Sesion getSesionRandomSampleGenerator() {
        return new Sesion().id(longCount.incrementAndGet());
    }
}
