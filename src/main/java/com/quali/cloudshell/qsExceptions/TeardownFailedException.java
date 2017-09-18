package com.quali.cloudshell.qsExceptions;


public class TeardownFailedException extends SandboxApiException {

    public TeardownFailedException(String sandbox, String message) {
        super("Teardown Failed for sandbox: " + sandbox + ", with exception: " + message);
    }
}
