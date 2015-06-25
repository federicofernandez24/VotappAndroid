package com.votapp.fede.votapp.events;

/**
 * Created by fede on 21/5/15.
 */
public class LoadedMeEvent {
    private Object valor;

    public LoadedMeEvent(Object object) {
        this.valor = object;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }
}
