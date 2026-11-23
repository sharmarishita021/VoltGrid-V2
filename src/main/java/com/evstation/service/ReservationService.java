package com.evstation.service;

import com.evstation.enums.BayStatus;
import com.evstation.exception.BayUnavailableException;
import com.evstation.exception.ReservationConflictException;
import com.evstation.layout.StationGridManager;
import com.evstation.model.*;
import com.evstation.repository.ReservationRepository;
import java.time.LocalDateTime;

/**
 * Service managing advance time-slot reservations, collision detection, and check-ins.
 */
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final StationGridManager gridManager;

    public ReservationService(ReservationRepository reservationRepository, StationGridManager gridManager) {
        this.reservationRepository = reservationRepository;
        this.gridManager = gridManager;
    }

    /**
     * Books an advance time slot on a specific bay.
     * Throws ReservationConflictException if an overlapping reservation exists.
     */
    public Reservation createReservation(Driver driver, Vehicle vehicle, String bayId, TimeSlot timeSlot)
            throws BayUnavailableException, ReservationConflictException {

        if (driver == null || vehicle == null || bayId == null || timeSlot == null) {
            throw new IllegalArgumentException("Reservation parameters cannot be null.");
        }

        ChargingBay bay = gridManager.getBayById(bayId);
        if (bay == null) {
            throw new BayUnavailableException("Bay with ID " + bayId + " does not exist.", "UNKNOWN");
        }

        // Validate connector compatibility
        if (bay.getConnectorType() != vehicle.getConnectorType()) {
            throw new BayUnavailableException(
                    String.format("Bay %s supports '%s', but vehicle %s requires '%s'.",
                            bayId, bay.getConnectorType(), vehicle.getLicensePlate(), vehicle.getConnectorType()),
                    vehicle.getConnectorType().name()
            );
        }

        // Check for time slot overlaps
        Reservation conflict = reservationRepository.findConflictingReservation(bayId, timeSlot);
        if (conflict != null) {
            throw new ReservationConflictException(
                    String.format("Time-slot conflict on %s: Already reserved from %s for %s.",
                            bayId, conflict.getTimeSlot(), conflict.getVehicle().getLicensePlate()),
                    bayId, conflict.getTimeSlot()
            );
        }

        Reservation newReservation = new Reservation(driver, vehicle, bay, timeSlot);
        reservationRepository.addReservation(newReservation);
        return newReservation;
    }

    /**
     * Cancels an existing reservation, freeing up the time slot.
     */
    public boolean cancelReservation(String reservationId) {
        Reservation res = reservationRepository.findById(reservationId);
        if (res != null && res.isActive()) {
            res.cancel();
            return true;
        }
        return false;
    }

    /**
     * Checks in a driver for their confirmed reservation and docks their vehicle.
     */
    public boolean checkInReservation(String reservationId) {
        Reservation res = reservationRepository.findById(reservationId);
        if (res == null || !res.isActive()) {
            return false;
        }

        ChargingBay bay = res.getBay();
        if (!bay.isAvailable()) {
            return false;
        }

        res.checkIn();
        bay.dockVehicle(res.getVehicle());
        return true;
    }
}
