package com.votapp.fede.votapp.events;

import android.location.Location;

/**
 * Created by fede on 21/5/15.
 */
public class EmergencyEvent {
    private Location valor;

    public EmergencyEvent(){}

    public EmergencyEvent(Location object) {
        this.valor = object;
    }

    public Location getValor() {
        return valor;
    }

    public void setValor(Location valor) {
        this.valor = valor;
    }
}
