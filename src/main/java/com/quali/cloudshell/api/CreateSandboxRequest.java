package com.quali.cloudshell.api;

import java.util.ArrayList;
import java.util.List;

public class CreateSandboxRequest
{
    public final String duration;
    public final String name;
    private List<Param> params;

    public CreateSandboxRequest(String duration, String name){
        this.duration = duration;
        this.name = name;
        this.params = new ArrayList<Param>();
    }

    public void addParam(Param param){
        this.params.add(param);
    }

    public String getName() {
        return this.name;
    }

    public String getDuration() {
        return this.duration;
    }

    public List<Param> getParams() {
        return this.params;
    }

    public static class Param {
        private String name;
        private String value;

        public Param(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

    }
}

