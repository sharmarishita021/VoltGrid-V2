package com.evstation.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Tracks an ongoing or completed charging session.
 */
public class ChargingSession {

    private final String sessionId;
    private final Vehicle vehicle;
    private final Driver driver;
    private final ChargingBay bay;
    private final LocalDateTime startTime;
    private LocalDateTime endTime;
    private final double initialSoc;
    private double finalSoc;
    private double energyDeliveredKwh;
    private boolean active;

    public ChargingSession(Vehicle vehicle, Driver driver, ChargingBay bay) {
        this.sessionId = "SES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.vehicle = vehicle;
        this.driver = driver;
        this.bay = bay;
        this.startTime = LocalDateTime.now();
        this.initialSoc = vehicle.getCurrentSocPercent();
        this.finalSoc = initialSoc;
        this.energyDeliveredKwh = 0.0;
        this.active = true;
    }

    public void complete(double finalSoc, double energyKwh) {
        this.endTime = LocalDateTime.now();
        this.finalSoc = finalSoc;
        this.energyDeliveredKwh = energyKwh;
        this.active = false;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Driver getDriver() {
        return driver;
    }

    public ChargingBay getBay() {
        return bay;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public double getInitialSoc() {
        return initialSoc;
    }

    public double getFinalSoc() {
        return finalSoc;
    }

    public double getEnergyDeliveredKwh() {
        return energyDeliveredKwh;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return String.format("Session[%s] %s @ %s | %.2f kWh (%.1f%% -> %.1f%%)",
                sessionId, vehicle.getLicensePlate(), bay.getBayId(),
                energyDeliveredKwh, initialSoc, finalSoc);
    }
}
