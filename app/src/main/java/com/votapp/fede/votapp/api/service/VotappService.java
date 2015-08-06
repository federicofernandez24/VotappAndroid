package com.votapp.fede.votapp.api.service;

import com.votapp.fede.votapp.Encuesta;
import com.votapp.fede.votapp.domain.Emergencia;
import com.votapp.fede.votapp.domain.Opinion;
import com.votapp.fede.votapp.domain.User;

import org.json.JSONObject;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by fede on 11/6/15.
 */
public interface VotappService {

    @POST("/usuario/loginEncuestador")
    void login(@Body User auth, Callback<Response> callback);

    @GET("/encuesta/getEncuestasByIdConsultora/{id}")
    void getEncuestasByIdConsultora(@Path("id") int groupId, Callback<Response> callback);

    @POST("/encuesta/protected/crearRespuesta")
    void altaOpinion(@Body Opinion opinion, Callback<Response> callback);

    @POST("/encuesta/protected/alertarEmergencia")
    void alertarEmergencia(@Body Emergencia locationEmergencia, Callback<Response> callback);
}