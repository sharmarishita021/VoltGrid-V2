package com.evstation.service;

import com.evstation.comparator.VehiclePriorityComparator;
import com.evstation.model.Driver;
import com.evstation.model.Vehicle;
import com.evstation.strategy.priority.IPriorityStrategy;
import java.util.*;

/**
 * Manages the unified charging wait line using a single PriorityQueue.
 * Prioritization and tie-breaking are handled by VehiclePriorityComparator.
 */
public class QueueManagerService {

    private final PriorityQueue<Vehicle> priorityQueue;

    public QueueManagerService(IPriorityStrategy strategy, Map<String, Driver> driverMap) {
        this.priorityQueue = new PriorityQueue<>(new VehiclePriorityComparator(strategy, driverMap));
    }

    public synchronized void enqueue(Vehicle vehicle) {
        if (vehicle != null) {
            priorityQueue.offer(vehicle);
        }
    }

    public synchronized Vehicle dequeue() {
        return priorityQueue.poll();
    }

    public synchronized Vehicle peek() {
        return priorityQueue.peek();
    }

    public synchronized boolean cancelSpot(String licensePlate) {
        if (licensePlate == null) return false;
        Iterator<Vehicle> it = priorityQueue.iterator();
        while (it.hasNext()) {
            Vehicle v = it.next();
            if (v.getLicensePlate().equalsIgnoreCase(licensePlate.trim())) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public synchronized int size() {
        return priorityQueue.size();
    }

    public synchronized List<Vehicle> getQueueSnapshot() {
        List<Vehicle> copy = new ArrayList<>(priorityQueue);
        // Sort snapshot to reflect true priority order
        copy.sort(priorityQueue.comparator());
        return Collections.unmodifiableList(copy);
    }
}
