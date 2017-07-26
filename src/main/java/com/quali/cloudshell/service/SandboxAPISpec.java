package com.quali.cloudshell.service;

import com.quali.cloudshell.api.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface SandboxAPISpec {
    @PUT("api/login")
    Call<String> login(@Body User user);

    @GET("api/v1/blueprints")
    Call<CreateSandboxResponse[]> getBlueprint();

    @GET("api/v1/sandboxes/{sandbox}")
    Call<SandboxDetailsResponse> getSandbox(
            @Path("sandbox") String sandbox);


    @POST("api/v1/blueprints/{blueprint}/start")
    Call<CreateSandboxResponse> createSandbox(
            @Path("blueprint") String blueprint,
            @Body CreateSandboxRequest sandboxRequest);

    @POST("api/v1/sandboxes/{sandbox}/stop")
    Call<DeleteSandboxResponse> stopSandbox(
            @Path("sandbox") String sandbox);

}
