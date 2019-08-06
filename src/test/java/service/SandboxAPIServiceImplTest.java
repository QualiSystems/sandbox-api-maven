package service;

import com.quali.cloudshell.api.*;
import com.quali.cloudshell.qsExceptions.SandboxApiException;
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
//
//    @Test
//    public void getBlueprints() throws Exception {
//        SandboxAPIServiceMock sandboxAPIServiceMock = new SandboxAPIServiceMock();
//        ResponseData<CreateSandboxResponse[]> blueprints = sandboxAPIServiceMock.getBlueprints();
//        CreateSandboxResponse[] data = blueprints.getData();
//        Assert.assertEquals(data.length, 1);
//        Assert.assertEquals(data[0].name, "my blueprint");
//    }
//
//    @Test
//    public void createSandbox() throws Exception {
//        SandboxAPIServiceImpl sandboxAPIService = new SandboxAPIServiceImpl(new SandboxServiceConnection("http://localhost:82", "admin", "admin", "Global", true));
//        ResponseData<CreateSandboxResponse> sandbox = sandboxAPIService.createSandbox("empty",
//                new CreateSandboxRequest("PT23H", "tomertomer"));
//
//        System.out.print(sandbox.getData());
//    }
//
//    @Test
//    public void loginSandbox() throws Exception {
//        SandboxAPIServiceImpl sandboxAPIService = new SandboxAPIServiceImpl(new SandboxServiceConnection("http://localhost:82", "admin", "admin", "Global", true));
//        ResponseData<String> login = sandboxAPIService.login();
//        System.out.print(login.getData());
//    }
////
////    @Test
////    public void deleteSandbox() throws Exception {
////        SandboxAPIServiceImpl sandboxAPIService = new SandboxAPIServiceImpl(new SandboxServiceConnection("http://localhost:82", "admin", "admin", "Global", false));
////        ResponseData<DeleteSandboxResponse> sandbox = sandboxAPIService.stopSandbox("d5a004bf-98aa-4d3a-a8d1-0fc8cd204f8b");
////        System.out.print(sandbox.getData());
////    }
////
//    @Test
//    public void getSandbox() throws Exception {
//        SandboxAPIServiceImpl sandboxAPIService = new SandboxAPIServiceImpl(new SandboxServiceConnection("http://localhost:82", "admin", "admin", "Global", true));
//        ResponseData<SandboxDetailsResponse> sandbox = sandboxAPIService.getSandbox("86152d6f-cb76-44f5-951c-a950c8eb49b8");
//        System.out.print(sandbox.getData());
//    }
//
//    @Test
//    public void getSandboxActivityFeeed() throws Exception {
//        SandboxAPIServiceImpl sandboxAPIService = new SandboxAPIServiceImpl(new SandboxServiceConnection("http://localhost:82", "admin", "admin", "Global", true));
//        ResponseData<SandboxActivity> sandboxActivity = sandboxAPIService.getSandboxActivity("86152d6f-cb76-44f5-951c-a950c8eb49b8", 100, null, null, null);
//        for (SandboxActivityEvent event: sandboxActivity.getData().events) {
//            if (event.event_text.contains("'Teardown' Blueprint command") && event.event_text.contains("failed")){
//                Assert.fail();
//            }
//        }
//        System.out.print(sandboxActivity.getData());
//    }
}