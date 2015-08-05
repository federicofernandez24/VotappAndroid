package com.votapp.fede.votapp.api;

import com.votapp.fede.votapp.Encuesta;
import com.votapp.fede.votapp.api.service.VotappService;
import com.votapp.fede.votapp.controller.AppController;
import com.votapp.fede.votapp.domain.Opinion;
import com.votapp.fede.votapp.domain.User;


import org.json.JSONObject;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.Response;

/**
 * Created by fede on 21/5/15.
 */
public class ConsultorApi extends RetrofitApi {

    private VotappService service;

    public ConsultorApi() {
        RestAdapter restAdapter = AppController.getRestAdapter();
        service = restAdapter.create(VotappService.class);
    }

    // Funcion Login de Encuestador que llama al servicio.
    public void login(User user, Callback<Response> callback) {
        service.login(user, callback);
    }

    public void getEncuestasByIdConsultora(int consultoraID, Callback<Response> callback) {
        service.getEncuestasByIdConsultora(consultoraID, callback);
    }
    public void altaOpinion(Opinion opinion, Callback<Response> callback){
        service.altaOpinion(opinion, callback);
    }

    public void alertarEmergencia(JSONObject locationEmergencia, Callback callback) {
        service.alertarEmergencia(locationEmergencia,callback);
    }
}
