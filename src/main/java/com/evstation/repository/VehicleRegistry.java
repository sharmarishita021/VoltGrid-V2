package com.evstation.repository;

import com.evstation.exception.DuplicateVehicleException;
import com.evstation.model.Driver;
import com.evstation.model.Vehicle;
import java.util.*;

/**
 * Manages registered vehicles and drivers.
 * Uses HashMap for O(1) lookups and HashSet for uniqueness.
 */
public class VehicleRegistry {

    private final Map<String, Vehicle> vehicleMap;
    private final Set<String> registeredVins;
    private final Map<String, Driver> driverMap;

    public VehicleRegistry() {
        this.vehicleMap = new HashMap<>();
        this.registeredVins = new HashSet<>();
        this.driverMap = new HashMap<>();
    }

    public synchronized void registerVehicle(Vehicle vehicle, Driver driver) throws DuplicateVehicleException {
        if (vehicle == null) {
            throw new IllegalArgumentException("Cannot register null vehicle.");
        }
        String vin = vehicle.getVin();
        if (registeredVins.contains(vin)) {
            throw new DuplicateVehicleException("Vehicle with VIN " + vin + " is already registered.", vin);
        }

        registeredVins.add(vin);
        vehicleMap.put(vin, vehicle);
        if (driver != null) {
            driverMap.put(vin, driver);
        }
    }

    public Vehicle findByVin(String vin) {
        if (vin == null) return null;
        return vehicleMap.get(vin.trim().toUpperCase());
    }

    public Driver findDriverByVin(String vin) {
        if (vin == null) return null;
        return driverMap.get(vin.trim().toUpperCase());
    }

    public Collection<Vehicle> getAllVehicles() {
        return Collections.unmodifiableCollection(vehicleMap.values());
    }

    public Map<String, Driver> getDriverMap() {
        return Collections.unmodifiableMap(driverMap);
    }

    public int getVehicleCount() {
        return vehicleMap.size();
    }
}
