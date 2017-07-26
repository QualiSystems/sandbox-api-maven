package com.quali.cloudshell.service;

import com.quali.cloudshell.api.User;

public class SandboxServiceConnection {
    public final String address;
    public final int port;
    public final int connectionTimeoutSec;
    public final User user;
    public final boolean ignoreSsl;
    public final String protocol;

    public SandboxServiceConnection(String protocol, String serverAddress, int port, int connectionTimeoutSec, String username, String password, String domain, boolean ignoreSsl){
        this.port = port;
        this.address= serverAddress;
        this.connectionTimeoutSec = connectionTimeoutSec;
        this.ignoreSsl = ignoreSsl;
        this.protocol = protocol;
        this.user = new User(username, password, domain);
    }
}