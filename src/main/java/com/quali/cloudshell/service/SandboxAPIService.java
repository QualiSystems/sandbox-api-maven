package com.quali.cloudshell.service;


import com.quali.cloudshell.api.*;
import com.quali.cloudshell.qsExceptions.ReserveBluePrintConflictException;
import com.quali.cloudshell.qsExceptions.SandboxApiException;

import java.io.IOException;

public interface SandboxAPIService
{
      ResponseData<String> login() throws RuntimeException, IOException;
      ResponseData<CreateSandboxResponse[]> getBlueprints() throws RuntimeException, IOException;
      ResponseData<CreateSandboxResponse> createSandbox(String blueprintId, CreateSandboxRequest sandboxRequest) throws RuntimeException, IOException, SandboxApiException;
      ResponseData<DeleteSandboxResponse> stopSandbox(String sandboxId) throws RuntimeException, IOException;
      ResponseData<SandboxDetailsResponse> getSandbox(String sandboxId) throws RuntimeException, IOException;
}
