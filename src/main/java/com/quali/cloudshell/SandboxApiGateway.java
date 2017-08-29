package com.quali.cloudshell;

import com.quali.cloudshell.api.CreateSandboxResponse;
import com.quali.cloudshell.qsExceptions.ReserveBluePrintConflictException;
import com.quali.cloudshell.qsExceptions.SandboxApiException;
import com.quali.cloudshell.logger.QsLogger;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;

public class SandboxApiGateway
{
    private final SandboxAPILogic logic;
    private final QsLogger logger;

    public SandboxApiGateway(String serverAddress, String user, String pw, String domain, boolean ignoreSSL, QsLogger qsLogger) throws SandboxApiException {
        this.logger = qsLogger;
        this.logic = new SandboxAPILogic(new QsServerDetails(serverAddress, user, pw, domain, ignoreSSL), qsLogger);
    }

    public SandboxApiGateway(QsLogger qsLogger, QsServerDetails qsServerDetails) throws SandboxApiException {
        this.logger = qsLogger;
        this.logic = new SandboxAPILogic(qsServerDetails, qsLogger);
    }

    public String GetSandboxDetails(String sandboxId)
            throws SandboxApiException, IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return logic.SandboxDetails(sandboxId).toString();
    }

    public ArrayList<String> GetBlueprintsNames()
            throws SandboxApiException, IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        ArrayList<String> names = new ArrayList<String>();
        for (CreateSandboxResponse blueprint : logic.GetBlueprints().getData()) names.add(blueprint.name);
        return names;
    }


    public String TryLogin() throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, SandboxApiException {
        return logic.Login();
    }

    public void StopSandbox(String sandboxId, boolean isSync)
            throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        logger.Info("Stopping Sandbox " + sandboxId);
        logic.StopSandbox(sandboxId, isSync);
    }

    public void WaitForSandBox(String sandboxId, String status, int timeoutSec, boolean ignoreSSL) throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        logic.WaitForSandBox(sandboxId,status,timeoutSec,ignoreSSL);
    }

    public String StartBlueprint(String blueprintName, int duration, boolean isSync, String sandboxName,  Map<String, String> parameters)
            throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {

        return Start(blueprintName, duration, isSync, sandboxName, parameters);
    }

    public String StartBlueprint(String blueprintName, int duration, boolean isSync, String sandboxName)
            throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {

        return Start(blueprintName, duration, isSync, sandboxName, null);
    }

    private String Start(String blueprintName, int duration, boolean isSync, String sandboxName, Map<String, String> parameters) throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        if (StringUtils.isBlank(sandboxName))
            sandboxName = blueprintName + "_" + java.util.UUID.randomUUID().toString().substring(0, 5);

        try {
            String sandboxId = logic.StartBluePrint(blueprintName, sandboxName, duration, isSync, parameters);
            logger.Info("StartBlueprint: sandbox started with id: " + sandboxId);
            return sandboxId;
        }
        catch (ReserveBluePrintConflictException ce){
            logger.Error(Constants.BLUEPRINT_CONFLICT_ERROR);
        }
        return null;
    }

    public void VerifyTeardownSucceeded (String sandboxId) throws IOException, SandboxApiException{
        logic.VerifyTeardownSucceeded(sandboxId);
    }
}
