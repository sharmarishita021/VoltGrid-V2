package com.evstation.repository;

import com.evstation.model.Reservation;
import com.evstation.model.TimeSlot;
import java.util.*;

/**
 * Manages advance reservations and detects time-slot collisions per bay.
 */
public class ReservationRepository {

    private final Map<String, Reservation> reservationById;
    private final Map<String, List<Reservation>> bayReservations;

    public ReservationRepository() {
        this.reservationById = new HashMap<>();
        this.bayReservations = new HashMap<>();
    }

    public synchronized void addReservation(Reservation reservation) {
        if (reservation == null) return;
        reservationById.put(reservation.getReservationId(), reservation);

        String bayId = reservation.getBay().getBayId();
        bayReservations.computeIfAbsent(bayId, k -> new ArrayList<>()).add(reservation);
    }

    /**
     * Checks if a proposed time slot collides with any confirmed reservation on the specified bay.
     * Returns the conflicting reservation, or null if clear.
     */
    public synchronized Reservation findConflictingReservation(String bayId, TimeSlot proposedSlot) {
        if (bayId == null || proposedSlot == null) return null;

        List<Reservation> list = bayReservations.get(bayId);
        if (list == null || list.isEmpty()) return null;

        for (Reservation existing : list) {
            if (existing.isActive() && existing.getTimeSlot().overlapsWith(proposedSlot)) {
                return existing;
            }
        }
        return null;
    }

    public Reservation findById(String reservationId) {
        if (reservationId == null) return null;
        return reservationById.get(reservationId.trim().toUpperCase());
    }

    public List<Reservation> getReservationsForBay(String bayId) {
        if (bayId == null) return Collections.emptyList();
        List<Reservation> list = bayReservations.get(bayId);
        return (list != null) ? Collections.unmodifiableList(list) : Collections.emptyList();
    }

    public Collection<Reservation> getAllReservations() {
        return Collections.unmodifiableCollection(reservationById.values());
    }
}
