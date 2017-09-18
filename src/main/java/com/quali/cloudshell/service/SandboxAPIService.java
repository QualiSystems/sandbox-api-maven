package com.quali.cloudshell.service;


import com.quali.cloudshell.api.*;
import com.quali.cloudshell.qsExceptions.SandboxApiException;

import java.io.IOException;

public interface SandboxAPIService
{
      ResponseData<String> login() throws RuntimeException, IOException, SandboxApiException;
      ResponseData<CreateSandboxResponse[]> getBlueprints() throws RuntimeException, IOException, SandboxApiException;
      ResponseData<CreateSandboxResponse> createSandbox(String blueprintId, CreateSandboxRequest sandboxRequest) throws RuntimeException, IOException, SandboxApiException;
      void stopSandbox(String sandboxId) throws RuntimeException, IOException, SandboxApiException;
      ResponseData<SandboxDetailsResponse> getSandbox(String sandboxId) throws RuntimeException, IOException, SandboxApiException;
      ResponseData<SandboxActivity> getSandboxActivity(String sandboxId, Integer tail , Long from_event_id, String since, Boolean error_only) throws RuntimeException, IOException, SandboxApiException;
}
