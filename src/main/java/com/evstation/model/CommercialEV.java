package com.evstation.model;

import com.evstation.enums.ConnectorType;

/**
 * Concrete subclass representing commercial fleet / taxi EVs.
 */
public class CommercialEV extends Vehicle {

    private final String fleetName;

    public CommercialEV(String vin, String licensePlate, String model,
                        ConnectorType connectorType, double batteryCapacityKwh,
                        double currentSocPercent, String fleetName) {
        super(vin, licensePlate, model, connectorType, batteryCapacityKwh, currentSocPercent);
        this.fleetName = fleetName;
    }

    @Override
    public String getClassification() {
        return "Fleet (" + fleetName + ")";
    }

    @Override
    public boolean isEmergency() {
        return false;
    }

    public String getFleetName() {
        return fleetName;
    }
}
