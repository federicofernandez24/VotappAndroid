package com.votapp.fede.votapp.events;

/**
 * Created by fede on 21/5/15.
 */
public class GetEncuestasEvent {

    private int consultoraID;

    public GetEncuestasEvent(int consultoraID) {
        this.consultoraID = consultoraID;
    }

    public int getConsultoraID() {
        return consultoraID;
    }

    public void setConsultoraID(int consultoraID) {
        this.consultoraID = consultoraID;
    }
}
