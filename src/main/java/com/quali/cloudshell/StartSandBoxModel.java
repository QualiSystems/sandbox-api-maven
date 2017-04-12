package com.quali.cloudshell;

import java.util.ArrayList;
import java.util.List;

public class StartSandBoxModel {
    private String name;
    private String duration;
    private List<Param> params;

    public StartSandBoxModel(String name, String duration) {
        this.name = name;
        this.duration = duration;
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

        Param(String name, String value) {
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
