package com.quali.cloudshell.service;

import com.quali.cloudshell.qsExceptions.SandboxApiException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class SandboxAPIRequestInterceptor implements Interceptor {

    private final SandboxAPIAuthProvider sandboxAPISpecProvider;

    SandboxAPIRequestInterceptor(SandboxAPIAuthProvider sandboxAPISpecProvider) {
        this.sandboxAPISpecProvider = sandboxAPISpecProvider;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        String authToken = sandboxAPISpecProvider.getAuthToken();

        Request newRequest = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .header("Authorization", "Basic " + authToken)
                .build();

        System.out.println(newRequest.url());

        return chain.proceed(newRequest);
    }
}

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