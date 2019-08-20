package com.quali.cloudshell.qsExceptions;

public class ExtendedSandboxApiException extends SandboxApiException {
    private String sandboxId;

    public ExtendedSandboxApiException(String message, String sandboxId) {
        super(message);
        this.sandboxId = sandboxId;
    }

    public String getSandboxId() {
        return sandboxId;
    }
}
