package com.quali.cloudshell.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;public class Sandbox implements Serializable
{
    @JsonProperty("id")
    public String id;
    @JsonProperty("name")
    public String name;
}


