package com.evstation.exception;

/**
 * Base custom checked exception for the VoltGrid EV system.
 */
public class EVStationException extends Exception {

    public EVStationException(String message) {
        super(message);
    }

    public EVStationException(String message, Throwable cause) {
        super(message, cause);
    }
}
