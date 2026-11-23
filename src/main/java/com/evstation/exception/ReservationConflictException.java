package com.evstation.exception;

import com.evstation.model.TimeSlot;

/**
 * Checked exception thrown when an advance reservation overlaps with an existing reservation on a bay.
 */
public class ReservationConflictException extends EVStationException {

    private final String bayId;
    private final TimeSlot conflictingSlot;

    public ReservationConflictException(String message, String bayId, TimeSlot conflictingSlot) {
        super(message);
        this.bayId = bayId;
        this.conflictingSlot = conflictingSlot;
    }

    public String getBayId() {
        return bayId;
    }

    public TimeSlot getConflictingSlot() {
        return conflictingSlot;
    }
}
