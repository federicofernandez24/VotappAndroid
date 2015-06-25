package com.votapp.fede.votapp.api;

/**
 * Created by fede on 11/6/15.
 */
public enum ApiTypes {

    USER_API(new ConsultorApi());

    private final RetrofitApi instance;

    private ApiTypes(RetrofitApi instance) {
        this.instance = instance;
    }

    public RetrofitApi getApiType() {
        return instance;
    }

}