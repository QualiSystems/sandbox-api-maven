package com.quali.cloudshell.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;public class Sandbox implements Serializable
{
    @JsonProperty("id")
public String id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("blueprint")
    public String blueprint;
    @JsonProperty("cloud_provider")
    public String cloudProvider;
//    @JsonProperty("services")
//    public List<Service> services;
}


