package com.quali.cloudshell.service;

import com.quali.cloudshell.api.User;

public class SandboxServiceConnection {
    public final String address;
    public final User user;
    public final boolean ignoreSsl;

    public SandboxServiceConnection(String serverAddress, String username, String password, String domain, boolean ignoreSsl){
        this.address= serverAddress;
        this.ignoreSsl = ignoreSsl;
        this.user = new User(username, password, domain);
    }
}