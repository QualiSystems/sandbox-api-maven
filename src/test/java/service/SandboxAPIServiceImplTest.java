package service;

import com.quali.cloudshell.api.CreateSandboxRequest;
import com.quali.cloudshell.api.CreateSandboxResponse;
import com.quali.cloudshell.api.ResponseData;
import com.quali.cloudshell.api.SandboxDetailsResponse;
import com.quali.cloudshell.service.SandboxAPIServiceImpl;
import com.quali.cloudshell.service.SandboxServiceConnection;
import org.junit.Assert;
import org.junit.Test;

public class SandboxAPIServiceImplTest {
    @Test
    public void login() throws Exception {
        SandboxAPIServiceMock sandboxAPIServiceMock = new SandboxAPIServiceMock();
        ResponseData<String> login = sandboxAPIServiceMock.login();
        String response = login.getData();
        Assert.assertEquals("GOzuXf+vpU2pOxzRQpwNOA==", response);
    }

    @Test
    public void getBlueprints() throws Exception {
        SandboxAPIServiceMock sandboxAPIServiceMock = new SandboxAPIServiceMock();
        ResponseData<CreateSandboxResponse[]> blueprints = sandboxAPIServiceMock.getBlueprints();
        CreateSandboxResponse[] data = blueprints.getData();
        Assert.assertEquals(data.length, 1);
        Assert.assertEquals(data[0].name, "my blueprint");
    }

    @Test
    public void createSandbox() throws Exception {
        SandboxAPIServiceImpl sandboxAPIService = new SandboxAPIServiceImpl(new SandboxServiceConnection("https://localhost:3443", "admin", "admin", "Global", true));
        ResponseData<CreateSandboxResponse> sandbox = sandboxAPIService.createSandbox("empty",
                new CreateSandboxRequest("PT23H", "tomertomer"));

        System.out.print(sandbox.getData());
    }

    @Test
    public void loginSandbox() throws Exception {
        SandboxAPIServiceImpl sandboxAPIService = new SandboxAPIServiceImpl(new SandboxServiceConnection("https://localhost:3443", "admin", "admin", "Global", true));
        ResponseData<String> login = sandboxAPIService.login();
        System.out.print(login.getData());
    }
//
//    @Test
//    public void deleteSandbox() throws Exception {
//        SandboxAPIServiceImpl sandboxAPIService = new SandboxAPIServiceImpl(new SandboxServiceConnection("http://localhost:82", "admin", "admin", "Global", false));
//        ResponseData<DeleteSandboxResponse> sandbox = sandboxAPIService.stopSandbox("d5a004bf-98aa-4d3a-a8d1-0fc8cd204f8b");
//        System.out.print(sandbox.getData());
//    }
//
    @Test
    public void getSandbox() throws Exception {
        SandboxAPIServiceImpl sandboxAPIService = new SandboxAPIServiceImpl(new SandboxServiceConnection("https://localhost:3443", "admin", "admin", "Global", true));
        ResponseData<SandboxDetailsResponse> sandbox = sandboxAPIService.getSandbox("d5a004bf-98aa-4d3a-a8d1-0fc8cd204f8b");
        System.out.print(sandbox.getData());
    }

}