package com.votapp.fede.votapp.api;

/**
 * Created by fede on 11/6/15.
 */
public enum ApiTypes {

    USER_API(new ConsultorApi());

    // DEBERIA SER FINAL Y UTILIZAR SOLO UNA INSTANCIA, PERO COMO VAMOS A HACER
    // ENDPOINTS DINAMICOS NECESITAMOS EDITAR ESTA VARIABLE, SE QUIEBRA PATTERN SINGLETON
    private RetrofitApi instance;

    private ApiTypes(RetrofitApi instance) {
        this.instance = instance;
    }

    // ACA ROMPE CON EL SINGLETON ESTA FUNCION DEBERIA IR, Y LA VARIABLE ANTERIOR DEBERIA SER PUBLICA
    public void updateEndPoint (){ this.instance = new ConsultorApi(); }

    public RetrofitApi getApiType() {
        return instance;
    }

}