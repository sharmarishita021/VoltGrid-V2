package com.evstation.model;

import com.evstation.enums.ConnectorType;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract base class representing an Electric Vehicle.
 */
public abstract class Vehicle implements Comparable<Vehicle> {

    private final String vin;
    private final String licensePlate;
    private final String model;
    private final ConnectorType connectorType;
    private final double batteryCapacityKwh;
    private double currentSocPercent;
    private final LocalDateTime arrivalTime;

    protected Vehicle(String vin, String licensePlate, String model,
                      ConnectorType connectorType, double batteryCapacityKwh, double currentSocPercent) {
        this.vin = vin;
        this.licensePlate = licensePlate;
        this.model = model;
        this.connectorType = connectorType;
        this.batteryCapacityKwh = batteryCapacityKwh;
        this.currentSocPercent = Math.max(0.0, Math.min(100.0, currentSocPercent));
        this.arrivalTime = LocalDateTime.now();
    }

    public abstract String getClassification();
    public abstract boolean isEmergency();

    @Override
    public int compareTo(Vehicle other) {
        if (other == null) return 1;
        // Natural order: lower battery percentage first
        return Double.compare(this.currentSocPercent, other.currentSocPercent);
    }

    public String getVin() {
        return vin;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getModel() {
        return model;
    }

    public ConnectorType getConnectorType() {
        return connectorType;
    }

    public double getBatteryCapacityKwh() {
        return batteryCapacityKwh;
    }

    public double getCurrentSocPercent() {
        return currentSocPercent;
    }

    public void setCurrentSocPercent(double currentSocPercent) {
        this.currentSocPercent = Math.max(0.0, Math.min(100.0, currentSocPercent));
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle vehicle)) return false;
        return Objects.equals(vin, vehicle.vin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vin);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) | SoC: %.1f%% | Plug: %s",
                getClassification(), licensePlate, model, currentSocPercent, connectorType);
    }
}
