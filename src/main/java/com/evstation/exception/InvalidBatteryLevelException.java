package com.evstation.exception;

/**
 * Unchecked RuntimeException thrown when battery state of charge is outside valid bounds [0.0, 100.0].
 */
public class InvalidBatteryLevelException extends RuntimeException {

    public InvalidBatteryLevelException(String message) {
        super(message);
    }
}
