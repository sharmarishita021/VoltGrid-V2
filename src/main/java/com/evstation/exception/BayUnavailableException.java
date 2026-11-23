package com.evstation.exception;

/**
 * Checked exception thrown when no compatible bay is free.
 */
public class BayUnavailableException extends EVStationException {

    private final String connectorType;

    public BayUnavailableException(String message, String connectorType) {
        super(message);
        this.connectorType = connectorType;
    }

    public String getConnectorType() {
        return connectorType;
    }
}
