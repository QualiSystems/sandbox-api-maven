package com.quali.cloudshell.service;

import com.quali.cloudshell.api.User;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;


interface SandboxAPISpecProvider{
    SandboxAPISpec getApi();
    User getUser();
}
public class SandboxAPIAuthInterceptor implements Interceptor {
    private String authToken;
    private SandboxAPISpecProvider sandboxAPISpecProvider;

    public SandboxAPIAuthInterceptor(SandboxAPISpecProvider sandboxAPISpecProvider){
        this.sandboxAPISpecProvider = sandboxAPISpecProvider;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        if (authToken == null && !request.url().toString().toLowerCase().contains("login")) {
            authToken = this.sandboxAPISpecProvider.getApi()
                    .login(this.sandboxAPISpecProvider.getUser()).execute().body();
        }

        Request newRequest = request.newBuilder()
                .addHeader("Authorization", "Basic " + authToken)
                .addHeader("Content-Type", "application/json")
                .build();

        Response proceed = chain.proceed(newRequest);
        return proceed;
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