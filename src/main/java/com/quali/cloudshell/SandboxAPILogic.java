/*   Copyright 2013, MANDIANT, Eric Lordahl
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.quali.cloudshell;

import com.quali.cloudshell.api.*;
import com.quali.cloudshell.logger.QsLogger;
import com.quali.cloudshell.qsExceptions.ReserveBluePrintConflictException;
import com.quali.cloudshell.qsExceptions.SandboxApiException;
import com.quali.cloudshell.qsExceptions.TeardownFailedException;
import com.quali.cloudshell.service.SandboxAPIService;
import com.quali.cloudshell.service.SandboxAPIServiceImpl;
import com.quali.cloudshell.service.SandboxServiceConnection;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class SandboxAPILogic {

    private final QsServerDetails server;
    private final QsLogger logger;
    private final SandboxAPIService sandboxAPIService;

    public SandboxAPILogic(QsServerDetails server, QsLogger logger) throws SandboxApiException {
        this.server = server;
        this.logger = logger;
        this.sandboxAPIService = new SandboxAPIServiceImpl(new SandboxServiceConnection(server.serverAddress, server.user, server.pw, server.domain, server.ignoreSSL));
    }

    public String StartBluePrint(String blueprintName, String sandboxName, int duration, boolean isSync, Map<String, String> parameters)
            throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {

        String sandboxDuration = "PT" + String.valueOf(duration) + "M";
        CreateSandboxRequest sandboxRequest = new CreateSandboxRequest(sandboxDuration, sandboxName);

        if (parameters != null) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                sandboxRequest.addParam(new CreateSandboxRequest.Param(entry.getKey(), entry.getValue()));
            }
        }

        ResponseData<CreateSandboxResponse> sandboxResponse = sandboxAPIService.createSandbox(blueprintName, sandboxRequest);

        if (sandboxResponse.getStatusCode() != 200){
            if (sandboxResponse.getError().contains(Constants.BLUEPRINT_CONFLICT_ERROR)){
                throw new ReserveBluePrintConflictException(blueprintName, sandboxResponse.getError());
            }
            throw new SandboxApiException(sandboxResponse.getError());
        }

        if (isSync)
        {
            WaitForSandBox(sandboxResponse.getData().id, "Ready", Constants.CONNECT_TIMEOUT_SECONDS, this.server.ignoreSSL);
        }

        return sandboxResponse.getData().id;
    }

    public void StopSandbox(String sandboxId, boolean isSync) throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        sandboxAPIService.stopSandbox(sandboxId);

        if (isSync)
        {
            WaitForSandBox(sandboxId, "Ended", Constants.CONNECT_TIMEOUT_SECONDS, this.server.ignoreSSL);
        }
    }

    public void WaitForSandBox(String sandboxId, String status, int timeoutSec, boolean ignoreSSL) throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        long startTime = System.currentTimeMillis();

        String sandboxStatus = GetSandBoxStatus(sandboxId);
        while (!sandboxStatus.equals(status) && (System.currentTimeMillis()-startTime) < timeoutSec*1000)
        {
            if (sandboxStatus.equals("Error"))
            {
                throw new SandboxApiException("Sandbox status is: Error");
            }
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sandboxStatus = GetSandBoxStatus(sandboxId);
        }
    }

    private String GetSandBoxStatus(String sandboxId) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, SandboxApiException {
        return SandboxDetails(sandboxId).state;
    }

    public SandboxDetailsResponse SandboxDetails(String sandboxId) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, SandboxApiException {
        return sandboxAPIService.getSandbox(sandboxId).getData();
    }

    public String Login() throws IOException, SandboxApiException {
        return sandboxAPIService.login().getData();
    }

    public ResponseData<CreateSandboxResponse[]> GetBlueprints() throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, SandboxApiException {
        return sandboxAPIService.getBlueprints();
    }

    public void VerifyTeardownSucceeded (String sandboxId) throws IOException, SandboxApiException {
        ResponseData<SandboxActivity> sandboxActivity = sandboxAPIService.getSandboxActivity(sandboxId, 100, null, null, null);
        for (SandboxActivityEvent event: sandboxActivity.getData().events) {
            if (event.event_text.contains("'Teardown' Blueprint command") && event.event_text.contains("failed")){
                throw new TeardownFailedException(sandboxId, event.event_text);
            }
        }
    }
}
