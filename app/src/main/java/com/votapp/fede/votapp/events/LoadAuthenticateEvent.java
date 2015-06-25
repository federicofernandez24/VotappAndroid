package com.votapp.fede.votapp.events;

/**
 * Created by fede on 21/5/15.
 */
public class LoadAuthenticateEvent {


    private String username;
    private String password;

    public LoadAuthenticateEvent(String username,String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
