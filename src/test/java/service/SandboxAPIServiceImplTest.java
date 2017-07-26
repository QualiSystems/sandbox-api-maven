package service;

import com.quali.cloudshell.api.*;
import com.quali.cloudshell.service.SandboxAPIServiceImpl;
import com.quali.cloudshell.service.SandboxServiceConnection;
import org.junit.Test;

/**
 * Created by Tomer.a on 7/25/2017.
 */
public class SandboxAPIServiceImplTest {
    @Test
    public void login() throws Exception {
        SandboxAPIServiceMock sandboxAPIServiceMock = new SandboxAPIServiceMock();
        ResponseData<String> login = sandboxAPIServiceMock.login(new User("admin", "admin", "Global"));
        String response = login.getData();
        System.out.print(response);
    }

    @Test
    public void getBlueprints() throws Exception {
        SandboxAPIServiceImpl sandboxAPIService = new SandboxAPIServiceImpl(new SandboxServiceConnection("https", "localhost", 3443, 3, "admin", "admin", "Global", true));
        ResponseData<CreateSandboxResponse[]> blueprints = sandboxAPIService.getBlueprints();
//        String response = blueprints.getData();
        System.out.print("a");
    }

    @Test
    public void createSandbox() throws Exception {
        SandboxAPIServiceImpl sandboxAPIService = new SandboxAPIServiceImpl(new SandboxServiceConnection("http", "localhost", 8032, 3, "admin", "admin", "Global", false));
        ResponseData<CreateSandboxResponse> sandbox = sandboxAPIService.createSandbox("fc631fe2-3f00-470c-87c5-d4072e392d55",
                new CreateSandboxRequest("PT23H", "tomertomer"));

        System.out.print(sandbox.getData());
    }

    @Test
    public void deleteSandbox() throws Exception {
        SandboxAPIServiceImpl sandboxAPIService = new SandboxAPIServiceImpl(new SandboxServiceConnection("http", "localhost", 8032, 3, "admin", "admin", "Global", false));
        ResponseData<DeleteSandboxResponse> sandbox = sandboxAPIService.stopSandbox("d5a004bf-98aa-4d3a-a8d1-0fc8cd204f8b");
        System.out.print(sandbox.getData());
    }

    @Test
    public void getSandbox() throws Exception {
        SandboxAPIServiceImpl sandboxAPIService = new SandboxAPIServiceImpl(new SandboxServiceConnection("http", "localhost", 8032, 3, "admin", "admin", "Global", false));
        ResponseData<SandboxDetailsResponse> sandbox = sandboxAPIService.getSandbox("d5a004bf-98aa-4d3a-a8d1-0fc8cd204f8b");
        System.out.print(sandbox.getData());
    }

}