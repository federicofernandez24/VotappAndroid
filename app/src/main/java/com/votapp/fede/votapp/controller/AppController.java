package com.votapp.fede.votapp.controller;

import android.app.Application;

import com.google.gson.Gson;
import com.votapp.fede.votapp.api.ApiTypes;
import com.votapp.fede.votapp.api.RetrofitApi;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by fede on 21/5/15.
 */
public class AppController extends Application {

    private static AppController instance;
    private static final String API_URL = "/Votapp/services";

    private static String API_ENDPOINT = "http://192.168.1.42";
    private static String API_PORT = "8080";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static synchronized AppController getInstance() {

        return instance;
    }

    public static RetrofitApi getApiOfType(ApiTypes type) {
        type.updateEndPoint();
        return type.getApiType();
    }

    public static String getApiUrl(){
        return AppController.API_ENDPOINT+":"+AppController.API_PORT+AppController.API_URL;
    }

    public static void setApiEndpoint (String ip, boolean https){

        if (https){
            AppController.API_ENDPOINT = "https://"+ip;
        }else{
            AppController.API_ENDPOINT = "http://"+ip;
        }
    }

    public static void setApiPort (String port){
        AppController.API_PORT = port;
    }

    public static RestAdapter getRestAdapter() {

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(getApiUrl())
                .setConverter(new GsonConverter(new Gson()));
        return builder.build();
    }

    }