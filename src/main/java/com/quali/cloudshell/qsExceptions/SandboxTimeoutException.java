package com.quali.cloudshell.qsExceptions;


public class SandboxTimeoutException extends SandboxApiException {

    public SandboxTimeoutException(String sandboxId) {
        super("Sandbox " + sandboxId + " reached the maximum timeout specified.");
    }
}
