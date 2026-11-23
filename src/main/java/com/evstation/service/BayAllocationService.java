package com.evstation.service;

import com.evstation.exception.BayUnavailableException;
import com.evstation.layout.StationGridManager;
import com.evstation.model.ChargingBay;
import com.evstation.model.Vehicle;

/**
 * Allocates available bays for immediate walk-in or queued vehicles.
 */
public class BayAllocationService {

    private final StationGridManager gridManager;

    public BayAllocationService(StationGridManager gridManager) {
        this.gridManager = gridManager;
    }

    public ChargingBay allocateBayForVehicle(Vehicle vehicle) throws BayUnavailableException {
        if (vehicle == null) {
            throw new IllegalArgumentException("Cannot allocate bay for null vehicle.");
        }

        ChargingBay bay = gridManager.findCompatibleAvailableBay(vehicle);
        if (bay == null) {
            throw new BayUnavailableException(
                    String.format("No free bay found supporting connector '%s' for vehicle %s.",
                            vehicle.getConnectorType(), vehicle.getLicensePlate()),
                    vehicle.getConnectorType().name()
            );
        }

        bay.dockVehicle(vehicle);
        return bay;
    }

    public void releaseBay(ChargingBay bay) {
        if (bay != null) {
            bay.releaseBay();
        }
    }
}
