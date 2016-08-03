package com.cloudshell.sandbox;

import com.cloudshell.sandbox.QsExceptions.ReserveBluePrintConflictException;
import com.cloudshell.sandbox.QsExceptions.SandboxApiException;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class SandboxApiGateway
{
    private final SandboxAPIProxy proxy;
    private final QsLogger logger;

    /*
        Tomer:
            TODO:
                Add Validations for parameters
                Add "test Connection" to make sure the gateway initialized properly
     */

    public SandboxApiGateway(String serverAddress, String user, String pw, String domain, boolean ignoreSSL, QsLogger qsLogger)
    {
        this.logger = qsLogger;
        this.proxy = new SandboxAPIProxy(new QsServerDetails(serverAddress, user, pw, domain, ignoreSSL), qsLogger);
    }

    public SandboxApiGateway(QsLogger qsLogger, QsServerDetails qsServerDetails)
    {
        this.logger = qsLogger;
        this.proxy = new SandboxAPIProxy(qsServerDetails, qsLogger);
    }

    public String GetSandboxDetails(String sandboxId)
            throws SandboxApiException, UnsupportedEncodingException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        logger.Info("GetSandboxDetails Starting to run");
        return proxy.SandboxDetails(sandboxId).toString();
    }

    public void StopSandbox(String sandboxId, boolean isSync)
            throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        logger.Info("StopSandbox Starting to run");
        proxy.StopSandbox(sandboxId, isSync);
    }

    public String startBlueprint(String blueprintName, int duration, boolean isSync, String sandboxName)
            throws SandboxApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnsupportedEncodingException {

        if (StringUtils.isBlank(sandboxName))
            sandboxName = blueprintName + "_" + java.util.UUID.randomUUID().toString().substring(0, 5);

        logger.Info("startBlueprint: sandbox name set to be " + sandboxName);

        try {
            String sandboxId = proxy.StartBluePrint(blueprintName, sandboxName, duration, isSync);
            logger.Info("startBlueprint: sandbox started with id: " + sandboxId);
            return sandboxId;
        }
        catch (ReserveBluePrintConflictException ce){
            logger.Error(Constants.BLUEPRINT_CONFLICT_ERROR);
        }
        return null;
    }
}
