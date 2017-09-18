package com.quali.cloudshell.api;

public class SandboxDetailsResponse
{
    public final String state;
    public final String name;
    public final String id;

    public SandboxDetailsResponse(String state, String name, String id){
        this.state = state;
        this.name = name;
        this.id = id;
    }
}

