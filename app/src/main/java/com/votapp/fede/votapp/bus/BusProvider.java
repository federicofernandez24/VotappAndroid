package com.votapp.fede.votapp.bus;

/**
 * Created by fede on 21/5/15.
 * Singleton proveedor de bus para las peticiones.
 */
public final class BusProvider {
    private static final MainThreadBus BUS = new MainThreadBus();

    public static MainThreadBus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}