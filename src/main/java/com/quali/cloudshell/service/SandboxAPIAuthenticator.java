package com.quali.cloudshell.service;

import com.quali.cloudshell.qsExceptions.SandboxApiException;
import okhttp3.*;

import java.io.IOException;

public class SandboxAPIAuthenticator implements Authenticator {

    private final SandboxAPIAuthProvider sandboxAPISpecProvider;

    SandboxAPIAuthenticator(SandboxAPIAuthProvider sandboxAPISpecProvider) {
        this.sandboxAPISpecProvider = sandboxAPISpecProvider;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if (response.request().url().toString().toLowerCase().contains("login")) {
            return null;
        }

        try {
            sandboxAPISpecProvider.loginAndSetAuthToken();
        } catch (SandboxApiException e) {
            throw new IOException(e.getMessage());
        }

        return response.request().newBuilder()
                .removeHeader("Authorization")
                .addHeader("Authorization", "Basic " + sandboxAPISpecProvider.getAuthToken())
                .build();
    }
}

//         Errors details from CloudShell-Sandbox-API
//{
//        "statusCode": 400,
//        "errorCategory": "WebApi",
//        "message": "The request didn't contain authentication."
//}

//{
//        "statusCode": 401,
//        "errorCategory": "WebApi",
//        "message": "Login failed for token: /SO7LjtzPEmVnCYj/1cFjw==. Please make sure the token is correct."
//}