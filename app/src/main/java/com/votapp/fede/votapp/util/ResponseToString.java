package com.votapp.fede.votapp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.client.Response;

/**
 * Created by fede on 25/7/15.
 */
public class ResponseToString {

  public String procesarBody( Response response){
    //Try to get response body
    BufferedReader reader = null;
    StringBuilder sb = new StringBuilder();
    try {

        reader = new BufferedReader(new InputStreamReader(response.getBody().in()));

        String line;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }


    return sb.toString();

  }
}
