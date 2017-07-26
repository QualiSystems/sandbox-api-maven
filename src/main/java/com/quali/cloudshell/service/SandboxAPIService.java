package com.quali.cloudshell.service;


import com.quali.cloudshell.api.*;

import java.io.IOException;

public interface SandboxAPIService
{
      ResponseData<String> login(User user) throws RuntimeException, IOException;
      ResponseData<CreateSandboxResponse[]> getBlueprints() throws RuntimeException, IOException;
      ResponseData<CreateSandboxResponse> createSandbox(String blueprintId, CreateSandboxRequest sandboxRequest) throws RuntimeException, IOException;
      ResponseData<DeleteSandboxResponse> stopSandbox(String blueprintId) throws RuntimeException, IOException;
      ResponseData<SandboxDetailsResponse> getSandbox(String sandbox) throws RuntimeException, IOException;
}
