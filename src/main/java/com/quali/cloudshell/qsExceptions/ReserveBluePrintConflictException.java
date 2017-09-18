package com.quali.cloudshell.qsExceptions;


public class ReserveBluePrintConflictException extends SandboxApiException {

    private String bluePrintIdentifier;

    public ReserveBluePrintConflictException(String bluePrintIdentifier, String message) {
        super("Blueprint: " + bluePrintIdentifier + " throw conflict exception: " + message);
    }

    public String getBluePrintIdentifier() {
        return bluePrintIdentifier;
    }
}
