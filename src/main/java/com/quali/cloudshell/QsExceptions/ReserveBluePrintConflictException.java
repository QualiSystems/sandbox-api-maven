package com.quali.cloudshell.QsExceptions;

/**
 * Created by Tomer.a on 8/2/2016.
 */
public class ReserveBluePrintConflictException extends SandboxApiException {

    private String bluePrintIdentifier;

    public ReserveBluePrintConflictException(String bluePrintIdentifier, String message) {
        super(String.format("Blueprint: %1$d throw conflict exception %2$d",bluePrintIdentifier, message));
    }

    public String getBluePrintIdentifier() {
        return bluePrintIdentifier;
    }
}
