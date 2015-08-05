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
        private static final String API_URL = "http://192.168.1.44:8080/Votapp/services";

        @Override
        public void onCreate() {
            super.onCreate();
            instance = this;
        }

        public static synchronized AppController getInstance() {

            return instance;
        }

        public static RetrofitApi getApiOfType(ApiTypes type) {

            return type.getApiType();
        }

        public static RestAdapter getRestAdapter() {

            RestAdapter.Builder builder = new RestAdapter.Builder()
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setEndpoint(API_URL)
                    .setConverter(new GsonConverter(new Gson()));
            return builder.build();
        }

    }