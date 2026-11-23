package com.evstation.strategy.priority;

import com.evstation.model.Driver;
import com.evstation.model.Vehicle;

/**
 * Strategy interface for calculating dynamic priority in the EV waiting queue.
 */
public interface IPriorityStrategy {

    /**
     * Computes priority score. Higher score = higher priority in queue.
     */
    double computeScore(Vehicle vehicle, Driver driver, long waitMinutes);
}
