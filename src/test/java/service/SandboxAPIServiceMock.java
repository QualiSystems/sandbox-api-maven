package service;


import com.quali.cloudshell.api.*;
import com.quali.cloudshell.service.SandboxAPIService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SandboxAPIServiceMock implements SandboxAPIService {

    @Override
    public ResponseData<String> login(User user) throws IOException {
        return ResponseData.ok("GOzuXf+vpU2pOxzRQpwNOA==",200);
    }

    @Override
    public ResponseData<CreateSandboxResponse[]> getBlueprints() throws RuntimeException, IOException {
        return null;
    }

    @Override
    public ResponseData<CreateSandboxResponse> createSandbox(String blueprintId, CreateSandboxRequest sandboxRequest) throws RuntimeException, IOException {
        return null;
    }

    @Override
    public ResponseData<DeleteSandboxResponse> stopSandbox(String blueprintId) throws RuntimeException, IOException {
        return null;
    }

    @Override
    public ResponseData<SandboxDetailsResponse> getSandbox(String sandbox) throws RuntimeException, IOException {
        return null;
    }
}
