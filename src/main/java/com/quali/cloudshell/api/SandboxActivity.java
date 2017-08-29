package com.quali.cloudshell.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SandboxActivity implements Serializable
{
    @JsonProperty("num_returned_events")
    public Long num_returned_events;
    @JsonProperty("more_pages")
    public Boolean more_pages;
    @JsonProperty("next_event_id")
    public Integer next_event_id;
    @JsonProperty("events")
    public SandboxActivityEvent[] events;
}


