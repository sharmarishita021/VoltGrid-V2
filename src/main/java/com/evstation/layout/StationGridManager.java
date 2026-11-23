package com.evstation.layout;

import com.evstation.config.StationConfig;
import com.evstation.enums.BayStatus;
import com.evstation.enums.ConnectorType;
import com.evstation.hardware.ChargerUnit;
import com.evstation.model.ChargingBay;
import com.evstation.model.Vehicle;

/**
 * Manages the physical 2D layout matrix of charging bays.
 * Demonstrates Multi-Dimensional Array handling (ChargingBay[][]).
 */
public class StationGridManager {

    private final int totalRows;
    private final int totalCols;
    private final ChargingBay[][] bayGrid;

    public StationGridManager() {
        this.totalRows = StationConfig.GRID_ROWS;
        this.totalCols = StationConfig.GRID_COLS;
        this.bayGrid = new ChargingBay[totalRows][totalCols];
        initializeGrid();
    }

    private void initializeGrid() {
        int bayNum = 1;
        for (int r = 0; r < totalRows; r++) {
            for (int c = 0; c < totalCols; c++) {
                String bayId = String.format("BAY-%02d", bayNum++);
                // Alternating connector types: Even index = CCS2 (60kW), Odd index = TYPE_2 (22kW)
                ConnectorType type = (c % 2 == 0) ? ConnectorType.CCS2 : ConnectorType.TYPE_2;
                double kw = (c % 2 == 0) ? 60.0 : 22.0;
                ChargerUnit unit = new ChargerUnit("CHG-" + bayId, kw, type);
                bayGrid[r][c] = new ChargingBay(bayId, r, c, type, unit);
            }
        }
    }

    public ChargingBay findCompatibleAvailableBay(Vehicle vehicle) {
        if (vehicle == null) return null;
        for (int r = 0; r < totalRows; r++) {
            for (int c = 0; c < totalCols; c++) {
                ChargingBay b = bayGrid[r][c];
                if (b.isAvailable() && b.getConnectorType() == vehicle.getConnectorType()) {
                    return b;
                }
            }
        }
        return null;
    }

    public ChargingBay getBayById(String bayId) {
        if (bayId == null) return null;
        for (int r = 0; r < totalRows; r++) {
            for (int c = 0; c < totalCols; c++) {
                if (bayGrid[r][c].getBayId().equalsIgnoreCase(bayId.trim())) {
                    return bayGrid[r][c];
                }
            }
        }
        return null;
    }

    public double calculateOccupancyPercentage() {
        int occupied = 0;
        int total = totalRows * totalCols;
        for (int r = 0; r < totalRows; r++) {
            for (int c = 0; c < totalCols; c++) {
                if (bayGrid[r][c].getStatus() != BayStatus.AVAILABLE) {
                    occupied++;
                }
            }
        }
        return (occupied * 100.0) / total;
    }

    public String renderAsciiGridMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n================ PHYSICAL LOT GRID (2D ARRAY) ================\n");
        for (int r = 0; r < totalRows; r++) {
            sb.append(String.format("Row %d | ", r));
            for (int c = 0; c < totalCols; c++) {
                ChargingBay b = bayGrid[r][c];
                String statusTag = switch (b.getStatus()) {
                    case AVAILABLE -> "[AVAIL]";
                    case CHARGING -> "[CHRG ]";
                    case OCCUPIED -> "[OCCUP]";
                    case RESERVED -> "[RSRVD]";
                    case MAINTENANCE -> "[MAINT]";
                };
                sb.append(String.format("%s:%s (%s)  ", b.getBayId(), statusTag, b.getConnectorType().name()));
            }
            sb.append("\n");
        }
        sb.append("===============================================================\n");
        return sb.toString();
    }

    public ChargingBay[][] getBayGrid() {
        return bayGrid;
    }
}
