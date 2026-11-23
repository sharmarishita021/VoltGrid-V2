package com.evstation.model;

import com.evstation.enums.ReservationStatus;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing an advance time-slot reservation for a bay.
 */
public class Reservation {

    private final String reservationId;
    private final Driver driver;
    private final Vehicle vehicle;
    private final ChargingBay bay;
    private final TimeSlot timeSlot;
    private ReservationStatus status;

    public Reservation(Driver driver, Vehicle vehicle, ChargingBay bay, TimeSlot timeSlot) {
        this.reservationId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.driver = driver;
        this.vehicle = vehicle;
        this.bay = bay;
        this.timeSlot = timeSlot;
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    public void checkIn() {
        this.status = ReservationStatus.CHECKED_IN;
    }

    public void complete() {
        this.status = ReservationStatus.COMPLETED;
    }

    public boolean isActive() {
        return this.status == ReservationStatus.CONFIRMED;
    }

    public String getReservationId() {
        return reservationId;
    }

    public Driver getDriver() {
        return driver;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ChargingBay getBay() {
        return bay;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation that)) return false;
        return Objects.equals(reservationId, that.reservationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId);
    }

    @Override
    public String toString() {
        return String.format("Reservation[%s] %s @ %s | Slot: %s | Status: %s",
                reservationId, vehicle.getLicensePlate(), bay.getBayId(), timeSlot, status);
    }
}
