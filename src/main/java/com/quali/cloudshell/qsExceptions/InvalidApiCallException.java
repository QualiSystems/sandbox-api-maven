package com.quali.cloudshell.qsExceptions;


public class InvalidApiCallException extends SandboxApiException {

    public InvalidApiCallException(String request) {
        super("Invalid API Call, make sure you are using API version that supports your request: " + request);
    }
}
