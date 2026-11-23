package com.evstation.validation;

import com.evstation.exception.InvalidBatteryLevelException;

/**
 * Validates vehicle identification, license plates, and parses numbers safely.
 */
public final class InputValidator {

    private InputValidator() {
        // Prevent instantiation
    }

    private static final String VIN_REGEX = "^[A-HJ-NPR-Z0-9]{17}$";
    private static final String PLATE_REGEX = "^[A-Z0-9 -]{4,10}$";

    public static boolean isValidVin(String vin) {
        if (vin == null) return false;
        return vin.trim().toUpperCase().matches(VIN_REGEX);
    }

    public static boolean isValidPlate(String plate) {
        if (plate == null) return false;
        return plate.trim().toUpperCase().matches(PLATE_REGEX);
    }

    public static double parseDoubleSafe(String input, double fallback) {
        if (input == null || input.trim().isEmpty()) return fallback;
        try {
            return Double.parseDouble(input.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static int parseIntSafe(String input, int fallback) {
        if (input == null || input.trim().isEmpty()) return fallback;
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static void validateBatterySoc(double soc) {
        if (soc < 0.0 || soc > 100.0) {
            throw new InvalidBatteryLevelException("Battery State of Charge must be 0-100%. Provided: " + soc);
        }
    }
}
