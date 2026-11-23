package com.evstation.hardware;

import com.evstation.enums.ConnectorType;

/**
 * Physical charging dispenser hardware.
 */
public class ChargerUnit {

    private final String serialNumber;
    private final double maxPowerKw;
    private final ConnectorType connectorType;
    private final double efficiency;

    public ChargerUnit(String serialNumber, double maxPowerKw, ConnectorType connectorType) {
        this.serialNumber = serialNumber;
        this.maxPowerKw = maxPowerKw;
        this.connectorType = connectorType;
        this.efficiency = 0.92;
    }

    public double getEffectivePowerKw() {
        return maxPowerKw * efficiency;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public double getMaxPowerKw() {
        return maxPowerKw;
    }

    public ConnectorType getConnectorType() {
        return connectorType;
    }

    @Override
    public String toString() {
        return String.format("%s (%.1f kW)", serialNumber, maxPowerKw);
    }
}
