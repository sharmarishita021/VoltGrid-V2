package com.evstation.concurrency;

import com.evstation.config.StationConfig;
import com.evstation.enums.BayStatus;
import com.evstation.model.ChargingBay;
import com.evstation.model.ChargingSession;
import com.evstation.model.Vehicle;

/**
 * Concurrent task simulating real-time EV charging on an independent background thread.
 * Enables multiple bays to charge simultaneously in parallel without blocking the main application.
 */
public class ChargingSimulationTask implements Runnable {

    private final ChargingSession session;
    private final double targetSoc;
    private volatile boolean finished;

    public ChargingSimulationTask(ChargingSession session, double targetSoc) {
        this.session = session;
        this.targetSoc = Math.min(100.0, Math.max(session.getInitialSoc(), targetSoc));
        this.finished = false;
    }

    @Override
    public void run() {
        Vehicle vehicle = session.getVehicle();
        ChargingBay bay = session.getBay();
        bay.setStatus(BayStatus.CHARGING);

        double currentSoc = vehicle.getCurrentSocPercent();
        double capacity = vehicle.getBatteryCapacityKwh();

        System.out.printf("\n>>> [STARTED IN BACKGROUND] %s is now charging at %s (Target: %.1f%%)\n",
                vehicle.getLicensePlate(), bay.getBayId(), targetSoc);

        try {
            while (currentSoc < targetSoc) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                Thread.sleep(StationConfig.CHARGE_STEP_DELAY_MS);
                currentSoc = Math.min(targetSoc, currentSoc + 5.0);
                vehicle.setCurrentSocPercent(currentSoc);
            }

            double energyDelivered = ((currentSoc - session.getInitialSoc()) / 100.0) * capacity;
            session.complete(currentSoc, energyDelivered);
            bay.setStatus(BayStatus.OCCUPIED); // Reverts from CHARGING to OCCUPIED when complete
            this.finished = true;

            System.out.printf("\n>>> [CHARGING FINISHED] %s at %s has reached %.1f%%! (Ready for checkout/billing)\n",
                    vehicle.getLicensePlate(), bay.getBayId(), currentSoc);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public ChargingSession getSession() {
        return session;
    }

    public double getTargetSoc() {
        return targetSoc;
    }
}
