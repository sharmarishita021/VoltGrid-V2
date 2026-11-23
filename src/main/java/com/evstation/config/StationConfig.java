package com.evstation.config;

/**
 * Global configuration constants for the VoltGrid V2 system.
 */
public final class StationConfig {

    private StationConfig() {
        // Prevent instantiation
    }

    public static final String STATION_NAME = "VoltGrid Central Hub";
    public static final double BASE_RATE_PER_KWH = 0.25;
    public static final double PEAK_MULTIPLIER = 1.35;
    public static final int PEAK_START_HOUR = 17; // 5:00 PM
    public static final int PEAK_END_HOUR = 21;   // 9:00 PM
    public static final int GRID_ROWS = 2;
    public static final int GRID_COLS = 4;
    public static final long CHARGE_STEP_DELAY_MS = 250L;
}
