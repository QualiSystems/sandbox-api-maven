package com.quali.cloudshell;

import com.quali.cloudshell.api.CreateSandboxResponse;
import com.quali.cloudshell.api.SandboxDetailsResponse;
import com.quali.cloudshell.qsExceptions.ReserveBluePrintConflictException;
import com.quali.cloudshell.qsExceptions.SandboxApiException;
import com.quali.cloudshell.logger.QsLogger;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SandboxApiGateway
{
    private final SandboxAPILogic logic;
    private final QsLogger logger;
    private final int responseTimeoutSec;

    public SandboxApiGateway(String serverAddress, String user, String pw, String domain, boolean ignoreSSL, QsLogger qsLogger) throws SandboxApiException {
        this.logger = qsLogger;
        this.logic = new SandboxAPILogic(new QsServerDetails(serverAddress, user, pw, domain, ignoreSSL), qsLogger);
        this.responseTimeoutSec = Constants.CONNECT_TIMEOUT_SECONDS;
    }

    public SandboxApiGateway(QsLogger qsLogger, QsServerDetails qsServerDetails) throws SandboxApiException {
        this.logger = qsLogger;
        this.logic = new SandboxAPILogic(qsServerDetails, qsLogger);
        this.responseTimeoutSec = Constants.CONNECT_TIMEOUT_SECONDS;
    }

    public SandboxApiGateway(QsLogger qsLogger, QsServerDetails qsServerDetails, int sandboxResponseTimeoutSec) throws SandboxApiException {
        this.logger = qsLogger;
        this.logic = new SandboxAPILogic(qsServerDetails, qsLogger);
        this.responseTimeoutSec = sandboxResponseTimeoutSec;
    }

    public SandboxApiGateway(String serverAddress, String user, String pw, String domain, boolean ignoreSSL, QsLogger qsLogger, int responseTimeoutSec) throws SandboxApiException {
        this.logger = qsLogger;
        this.logic = new SandboxAPILogic(new QsServerDetails(serverAddress, user, pw, domain, ignoreSSL), qsLogger);
        this.responseTimeoutSec = responseTimeoutSec;
    }

    public SandboxDetailsResponse GetSandboxDetails(String sandboxId)
            throws SandboxApiException, IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return logic.SandboxDetails(sandboxId);
    }

    public ArrayList<String> GetBlueprintsNames()
            throws SandboxApiException, IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        ArrayList<String> names = new ArrayList<String>();
        for (CreateSandboxResponse blueprint : logic.GetBlueprints().getData()) names.add(blueprint.name);
        return names;
    }

    public Map<String, String> TryParseBlueprintParams(String params) throws SandboxApiException {
        if (params != null && !params.isEmpty()) {
            Map<String, String> map = new HashMap<>();
            String[] parameters = params.split(";");
            for (String param: parameters) {
                String[] split = param.trim().split("=");
                if (split.length < 2) throw new SandboxApiException("Failed to parse blueprint parameters");
                map.put(split[0], split[1]);
            }
            return map;
        }
        return null;
    }


    public String TryLogin() throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, SandboxApiException {
        return logic.Login();
    }

    public void StopSandbox(String sandboxId, boolean isSync)
            throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        logger.info("Stopping Sandbox " + sandboxId);
        logic.StopSandbox(sandboxId, isSync, this.responseTimeoutSec);
    }

    public void WaitForSandBox(String sandboxId, String status, int timeoutSec, boolean ignoreSSL) throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        logic.WaitForSandBox(sandboxId,status,timeoutSec);
    }

    public String TryStartBlueprint(String blueprintName, int duration, boolean isSync, String sandboxName,  Map<String, String> parameters, int timeoutIfSandboxUnavailable)
            throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {

        try {
            return startSandbox(blueprintName, duration, isSync, sandboxName, parameters, responseTimeoutSec);
        }

        catch (ReserveBluePrintConflictException ce){
            logger.info("CloudShell Blueprint is unavailable, retrying to reserve...");
            long startTime = System.currentTimeMillis();
            while ((System.currentTimeMillis()-startTime) < timeoutIfSandboxUnavailable * 60 * 1000)
            {
                try {
                    return startSandbox(blueprintName, duration, isSync, sandboxName, parameters, responseTimeoutSec);
                }
                catch (ReserveBluePrintConflictException e1){
                    try {
                        Thread.sleep(60 * 1000);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                }
            }
            throw new ReserveBluePrintConflictException(blueprintName, Constants.BLUEPRINT_CONFLICT_ERROR);
        }
    }

    public void VerifyTeardownSucceeded (String sandboxId) throws IOException, SandboxApiException{
        logic.VerifyTeardownSucceeded(sandboxId);
    }

    private String startSandbox(String blueprintName, int duration, boolean isSync, String sandboxName, Map<String, String> parameters, int responseTimeoutSec) throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        String sandboxId = logic.StartBlueprint(blueprintName, sandboxName, duration, isSync, parameters, responseTimeoutSec);
        logger.info("CloudShell: Sandbox " + sandboxId + " started successfully.");
        return sandboxId;
    }
}
