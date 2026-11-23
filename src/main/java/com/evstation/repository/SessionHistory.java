package com.evstation.repository;

import com.evstation.model.ChargingSession;
import java.util.*;

/**
 * Manages active and completed charging sessions.
 * Uses ArrayList for indexed history and LinkedHashMap for active sessions.
 */
public class SessionHistory {

    private final List<ChargingSession> completedSessions;
    private final Map<String, ChargingSession> activeSessions;

    public SessionHistory() {
        this.completedSessions = new ArrayList<>();
        this.activeSessions = new LinkedHashMap<>();
    }

    public synchronized void recordNewSession(ChargingSession session) {
        if (session != null) {
            activeSessions.put(session.getSessionId(), session);
        }
    }

    public synchronized void completeSession(ChargingSession session) {
        if (session != null) {
            activeSessions.remove(session.getSessionId());
            completedSessions.add(session);
        }
    }

    public ChargingSession findActiveSessionByBayId(String bayId) {
        if (bayId == null) return null;
        for (ChargingSession s : activeSessions.values()) {
            if (s.getBay().getBayId().equalsIgnoreCase(bayId.trim())) {
                return s;
            }
        }
        return null;
    }

    public List<ChargingSession> getCompletedSessions() {
        return Collections.unmodifiableList(completedSessions);
    }

    public Map<String, ChargingSession> getActiveSessions() {
        return Collections.unmodifiableMap(activeSessions);
    }
}
