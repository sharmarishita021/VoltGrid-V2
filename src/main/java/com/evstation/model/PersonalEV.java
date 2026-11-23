package com.evstation.model;

import com.evstation.enums.ConnectorType;

/**
 * Concrete subclass representing private passenger EVs.
 */
public class PersonalEV extends Vehicle {

    public PersonalEV(String vin, String licensePlate, String model,
                      ConnectorType connectorType, double batteryCapacityKwh, double currentSocPercent) {
        super(vin, licensePlate, model, connectorType, batteryCapacityKwh, currentSocPercent);
    }

    @Override
    public String getClassification() {
        return "Personal EV";
    }

    @Override
    public boolean isEmergency() {
        return false;
    }
}
