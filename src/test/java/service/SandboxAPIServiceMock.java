package service;


import com.quali.cloudshell.api.*;
import com.quali.cloudshell.service.SandboxAPIService;

import java.io.IOException;

public class SandboxAPIServiceMock implements SandboxAPIService {

    @Override
    public ResponseData<String> login() throws RuntimeException, IOException {
        return ResponseData.ok("GOzuXf+vpU2pOxzRQpwNOA==",200);
    }

    @Override
    public ResponseData<CreateSandboxResponse[]> getBlueprints() throws RuntimeException, IOException {
        CreateSandboxResponse createSandboxResponse = new CreateSandboxResponse();
        createSandboxResponse.id = "b4e850d6-acde-4ace-a902-b4db7e1fd051";
        createSandboxResponse.name = "my blueprint";
        CreateSandboxResponse[] createSandboxResponses = new CreateSandboxResponse[] {createSandboxResponse};
        return ResponseData.ok( createSandboxResponses, 200);
    }

    @Override
    public ResponseData<CreateSandboxResponse> createSandbox(String blueprintId, CreateSandboxRequest sandboxRequest) throws RuntimeException, IOException {
        return null;
    }

    @Override
    public ResponseData<DeleteSandboxResponse> stopSandbox(String sandboxId) throws RuntimeException, IOException {
        return null;
    }

    @Override
    public ResponseData<SandboxDetailsResponse> getSandbox(String sandboxId) throws RuntimeException, IOException {
        return null;
    }
}
