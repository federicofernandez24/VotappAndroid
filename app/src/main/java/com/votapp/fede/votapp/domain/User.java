package com.votapp.fede.votapp.domain;

/**
 * Created by fede on 21/5/15.
 */
public class User {

    private String username;
    private String password;

    public User(){}
    User(String username,String password){
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
