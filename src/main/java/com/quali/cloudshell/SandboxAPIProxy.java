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

import com.quali.cloudshell.QsExceptions.ReserveBluePrintConflictException;
import com.quali.cloudshell.QsExceptions.SandboxApiException;
import net.sf.json.JSONObject;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class SandboxAPIProxy {

    private final QsServerDetails server;
    private final QsLogger logger;

    public SandboxAPIProxy(QsServerDetails server, QsLogger logger){
        this.server = server;
        this.logger = logger;
    }

    public String StartBluePrint(String blueprintName, String sandboxName, int duration, boolean isSync)
            throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnsupportedEncodingException {
        RestResponse response = Login();

        String url = GetBaseUrl() + Constants.BLUEPRINTS_URI  + URLEncoder.encode(blueprintName, "UTF-8") +"/start";

        StringEntity params = null;
        String sandboxDuration = "PT" + String.valueOf(duration) + "M";

        String string = "{\"name\":\"" + sandboxName + "\",\"duration\":\"" + sandboxDuration + "\"}";
        params = new StringEntity(string);

        JSONObject result = HTTPWrapper.ExecutePost(url, response.getContent(), params, this.server.ignoreSSL);

        if (result.containsKey(Constants.ERROR_CATEGORY)) {
            String message = result.get(Constants.MESSAGE).toString();
            if (message.equals(Constants.BLUEPRINT_CONFLICT_ERROR)){
                throw new ReserveBluePrintConflictException(blueprintName,message);
            }
            throw new SandboxApiException(blueprintName);
        }
        String newSb = result.getString("id");

        if (isSync)
        {
            WaitForSandBox(newSb, "Ready", 300, this.server.ignoreSSL);
        }

        return newSb;
    }

    public void StopSandbox(String sandboxId, boolean isSync) throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        RestResponse response = Login();
        String url = GetBaseUrl() + Constants.SANDBOXES_URI  + sandboxId + "/stop";
        JSONObject result = HTTPWrapper.ExecutePost(url, response.getContent(), null, this.server.ignoreSSL);
        if (result.containsKey(Constants.ERROR_CATEGORY)) {
            throw new SandboxApiException("Failed to stop blueprint: " + result);
        }
        try
        {
            if (isSync)
            {
                WaitForSandBox(sandboxId, "Ended", 300, this.server.ignoreSSL);
            }
        }
        catch (Exception e)
        {
        }
    }

    private RestResponse Login() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        return HTTPWrapper.InvokeLogin(GetBaseUrl(),
                this.server.user,
                this.server.pw,
                this.server.domain,
                this.server.ignoreSSL);
    }

    private void WaitForSandBox(String sandboxId, String status, int timeoutSec, boolean ignoreSSL) throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
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

    private String GetSandBoxStatus(String sb) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return SandboxDetails(sb).getString("state");
    }

    public JSONObject SandboxDetails(String sb) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        RestResponse response = Login();
        String url = GetBaseUrl() + Constants.SANDBOXES_URI + sb;
        RestResponse result = HTTPWrapper.ExecuteGet(url, response.getContent(), this.server.ignoreSSL);

        JSONObject j = JSONObject.fromObject(result.getContent());

        if (j.toString().contains(Constants.ERROR_CATEGORY)) {
            throw new RuntimeException("Failed to get sandbox details: " + j);
        }
        return j;
    }

    private String GetBaseUrl()
    {
        return this.server.serverAddress + "/Api" + Constants.API_VERSION;
    }

}
