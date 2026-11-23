package com.evstation.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Immutable value object representing a scheduled time interval.
 * Handles overlap detection for slot reservations.
 */
public class TimeSlot implements Comparable<TimeSlot> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time cannot be null.");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be strictly after start time.");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Checks if this time slot collides/overlaps with another time slot.
     * Formula: (startA < endB) and (endA > startB)
     */
    public boolean overlapsWith(TimeSlot other) {
        if (other == null) return false;
        return this.startTime.isBefore(other.endTime) && this.endTime.isAfter(other.startTime);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public int compareTo(TimeSlot other) {
        if (other == null) return 1;
        int cmp = this.startTime.compareTo(other.startTime);
        return (cmp != 0) ? cmp : this.endTime.compareTo(other.endTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSlot timeSlot)) return false;
        return Objects.equals(startTime, timeSlot.startTime) && Objects.equals(endTime, timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }

    @Override
    public String toString() {
        return startTime.format(FORMATTER) + " -> " + endTime.format(FORMATTER);
    }
}
