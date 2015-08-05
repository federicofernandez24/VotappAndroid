package com.votapp.fede.votapp.events;

/**
 * Created by fede on 21/5/15.
 */
public class LoadedMeEvent {
    private String valor;

    public LoadedMeEvent(String object) {
        this.valor = object;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
