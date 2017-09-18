package com.quali.cloudshell.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SandboxActivityEvent implements Serializable
{
    @JsonProperty("id")
    public String id;
    @JsonProperty("event_type")
    public String event_type;
    @JsonProperty("event_text")
    public String event_text;
    @JsonProperty("output")
    public String output;
    @JsonProperty("time")
    public String time;
}


