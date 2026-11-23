package com.evstation.exception;

/**
 * Checked exception thrown when registering a vehicle with an existing VIN.
 */
public class DuplicateVehicleException extends EVStationException {

    private final String vin;

    public DuplicateVehicleException(String message, String vin) {
        super(message);
        this.vin = vin;
    }

    public String getVin() {
        return vin;
    }
}
