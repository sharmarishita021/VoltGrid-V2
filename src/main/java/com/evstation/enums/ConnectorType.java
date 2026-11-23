package com.evstation.enums;

/**
 * Supported EV charging connector types.
 */
public enum ConnectorType {
    CCS2("Combined Charging System 2 (DC Fast)"),
    TYPE_2("Type 2 Mennekes (AC Standard)"),
    CHADEMO("CHAdeMO (DC Fast)"),
    GB_T("GB/T Standard");

    private final String description;

    ConnectorType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
