package com.evstation.hardware;

/**
 * Digital energy meter measuring electricity dispensed in kWh.
 */
public class EnergyMeter {

    private final String meterId;
    private double totalKwh;

    public EnergyMeter(String meterId) {
        this.meterId = meterId;
        this.totalKwh = 0.0;
    }

    public synchronized void recordConsumption(double kwh) {
        if (kwh > 0) {
            this.totalKwh += kwh;
        }
    }

    public String getMeterId() {
        return meterId;
    }

    public double getTotalKwh() {
        return totalKwh;
    }

    @Override
    public String toString() {
        return String.format("Meter[%s: %.2f kWh]", meterId, totalKwh);
    }
}
