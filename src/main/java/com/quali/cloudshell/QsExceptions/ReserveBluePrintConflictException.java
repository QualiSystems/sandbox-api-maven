package com.quali.cloudshell.QsExceptions;

/**
 * Created by Tomer.a on 8/2/2016.
 */
public class ReserveBluePrintConflictException extends SandboxApiException {

    private String bluePrintIdentifier;

    public ReserveBluePrintConflictException(String bluePrintIdentifier, String message) {
        super("Blueprint: " + bluePrintIdentifier + " throw conflict exception " + message);
    }

    public String getBluePrintIdentifier() {
        return bluePrintIdentifier;
    }
}
