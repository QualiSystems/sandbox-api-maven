package com.quali.cloudshell.service;

import com.quali.cloudshell.api.User;
import com.quali.cloudshell.qsExceptions.SandboxApiException;

import java.io.IOException;

interface SandboxAPIAuthProvider {
    String getAuthToken();
    void loginAndSetAuthToken() throws IOException, SandboxApiException;
}