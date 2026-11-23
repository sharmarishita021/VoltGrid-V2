package com.evstation.comparator;

import com.evstation.model.Driver;
import com.evstation.model.Vehicle;
import com.evstation.strategy.priority.IPriorityStrategy;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;

/**
 * Comparator that sorts vehicles by priority score descending.
 * Uses arrival time as a strict tie-breaker when priority scores are identical.
 */
public class VehiclePriorityComparator implements Comparator<Vehicle> {

    private final IPriorityStrategy strategy;
    private final Map<String, Driver> driverMap;

    public VehiclePriorityComparator(IPriorityStrategy strategy, Map<String, Driver> driverMap) {
        this.strategy = strategy;
        this.driverMap = driverMap;
    }

    @Override
    public int compare(Vehicle v1, Vehicle v2) {
        if (v1 == null && v2 == null) return 0;
        if (v1 == null) return 1;
        if (v2 == null) return -1;

        LocalDateTime now = LocalDateTime.now();
        long wait1 = Duration.between(v1.getArrivalTime(), now).toMinutes();
        long wait2 = Duration.between(v2.getArrivalTime(), now).toMinutes();

        Driver d1 = (driverMap != null) ? driverMap.get(v1.getVin()) : null;
        Driver d2 = (driverMap != null) ? driverMap.get(v2.getVin()) : null;

        double score1 = strategy.computeScore(v1, d1, wait1);
        double score2 = strategy.computeScore(v2, d2, wait2);

        // Primary: Highest priority score first
        int scoreCompare = Double.compare(score2, score1);
        if (scoreCompare != 0) {
            return scoreCompare;
        }

        // Tie-breaker: Earlier arrival time wins (FIFO)
        return v1.getArrivalTime().compareTo(v2.getArrivalTime());
    }
}
