package com.quali.cloudshell.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class User implements Serializable {

    public User(String username, String password, String domain){
        this.username = username;
        this.password= password;
        this.domain = domain;
    }

    @JsonProperty("username")
    public String username;
    @JsonProperty("password")
    public String password;
    @JsonProperty("domain")
    public String domain;
}
