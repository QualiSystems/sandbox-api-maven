package com.quali.cloudshell.api;

public class CreateSandboxRequest
{
    public final String duration;
    public final String name;

    public CreateSandboxRequest(String duration, String name){
        this.duration = duration;
        this.name = name;
    }
}

