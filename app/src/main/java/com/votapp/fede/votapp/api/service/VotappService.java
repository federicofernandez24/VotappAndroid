package com.votapp.fede.votapp.api.service;

import com.votapp.fede.votapp.domain.User;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by fede on 11/6/15.
 */
public interface VotappService {

    @POST("/usuario/loginEncuestador")
    void login(@Body User auth, Callback<Response> callback);
}