package com.votapp.fede.votapp.domain;

/**
 * Created by fede on 27/7/15.
 */
public class Opinion {
    public int getIdEncuesta() {
        return idEncuesta;
    }

    public void setIdEncuesta(int idEncuesta) {
        this.idEncuesta = idEncuesta;
    }

    public int getIdCandidato() {
        return idCandidato;
    }

    public void setIdCandidato(int idCandidato) {
        this.idCandidato = idCandidato;
    }

    public int getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(int idPartido) {
        this.idPartido = idPartido;
    }

    public int getIdLista() {
        return idLista;
    }

    public void setIdLista(int lista) {
        this.idLista = lista;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getNivelEstudio() {
        return nivelEstudio;
    }

    public void setNivelEstudio(String nivelEstudio) {
        this.nivelEstudio = nivelEstudio;
    }

    public double getIngresos() { return ingresos; }

    public void setIngresos(double ingresos) { this.ingresos = ingresos; }

    public boolean getTrabaja() { return trabaja; }

    public void setTrabaja(boolean trabaja) { this.trabaja = trabaja; }

    private int idEncuesta;
    private int idCandidato;
    private int idPartido;
    private int idLista;
    private int edad;
    private String sexo;
    private String nivelEstudio;
    private boolean trabaja;
    private double ingresos;
}
