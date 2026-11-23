package com.evstation.strategy.priority;

import com.evstation.model.Driver;
import com.evstation.model.Vehicle;

/**
 * Unified priority scheduling policy:
 * 1. Emergency vehicles receive top override priority (>= 1000 pts).
 * 2. Regular vehicles are scored via battery depletion (100 - SoC), wait duration, and loyalty tier.
 */
public class UnifiedPriorityStrategy implements IPriorityStrategy {

    @Override
    public double computeScore(Vehicle vehicle, Driver driver, long waitMinutes) {
        if (vehicle == null) return 0.0;

        // Rule 1: Emergency vehicles always get highest priority
        if (vehicle.isEmergency()) {
            return 1000.0 + (waitMinutes * 0.5);
        }

        // Rule 2: Battery depletion score (0 to 100)
        double batteryDepletion = (100.0 - vehicle.getCurrentSocPercent());
        double batteryScore = batteryDepletion * 1.2;

        // Rule 3: Wait time score (0.5 points per minute)
        double waitScore = waitMinutes * 0.5;

        // Rule 4: Membership tier multiplier
        double memberMultiplier = (driver != null) ? driver.getMembershipTier().getPriorityMultiplier() : 1.0;

        return (batteryScore * memberMultiplier) + waitScore;
    }
}
