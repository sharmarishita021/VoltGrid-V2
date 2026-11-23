package com.evstation.comparator;

import com.evstation.model.Reservation;
import java.util.Comparator;

/**
 * Comparator that sorts reservations chronologically by their starting time.
 */
public class ReservationTimeComparator implements Comparator<Reservation> {

    @Override
    public int compare(Reservation r1, Reservation r2) {
        if (r1 == null && r2 == null) return 0;
        if (r1 == null) return 1;
        if (r2 == null) return -1;

        return r1.getTimeSlot().compareTo(r2.getTimeSlot());
    }
}
