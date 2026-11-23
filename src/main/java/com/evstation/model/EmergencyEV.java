package com.evstation.model;

import com.evstation.enums.ConnectorType;

/**
 * Concrete subclass representing emergency service vehicles (ambulances, police).
 */
public class EmergencyEV extends Vehicle {

    private final String agency;

    public EmergencyEV(String vin, String licensePlate, String model,
                       ConnectorType connectorType, double batteryCapacityKwh,
                       double currentSocPercent, String agency) {
        super(vin, licensePlate, model, connectorType, batteryCapacityKwh, currentSocPercent);
        this.agency = agency;
    }

    @Override
    public String getClassification() {
        return "EMERGENCY (" + agency + ")";
    }

    @Override
    public boolean isEmergency() {
        return true;
    }

    public String getAgency() {
        return agency;
    }
}
