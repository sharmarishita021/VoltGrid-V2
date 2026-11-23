package com.evstation.model;

import com.evstation.enums.BayStatus;
import com.evstation.enums.ConnectorType;
import com.evstation.hardware.ChargerUnit;

/**
 * Represents a physical bay/slot in the charging station lot.
 */
public class ChargingBay {

    private final String bayId;
    private final int row;
    private final int col;
    private final ConnectorType connectorType;
    private final ChargerUnit chargerUnit;
    private BayStatus status;
    private Vehicle dockedVehicle;

    public ChargingBay(String bayId, int row, int col, ConnectorType connectorType, ChargerUnit chargerUnit) {
        this.bayId = bayId;
        this.row = row;
        this.col = col;
        this.connectorType = connectorType;
        this.chargerUnit = chargerUnit;
        this.status = BayStatus.AVAILABLE;
        this.dockedVehicle = null;
    }

    public boolean isAvailable() {
        return this.status == BayStatus.AVAILABLE && this.dockedVehicle == null;
    }

    public void dockVehicle(Vehicle vehicle) {
        this.dockedVehicle = vehicle;
        this.status = BayStatus.OCCUPIED;
    }

    public void releaseBay() {
        this.dockedVehicle = null;
        this.status = BayStatus.AVAILABLE;
    }

    public String getBayId() {
        return bayId;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public ConnectorType getConnectorType() {
        return connectorType;
    }

    public ChargerUnit getChargerUnit() {
        return chargerUnit;
    }

    public BayStatus getStatus() {
        return status;
    }

    public void setStatus(BayStatus status) {
        this.status = status;
    }

    public Vehicle getDockedVehicle() {
        return dockedVehicle;
    }

    @Override
    public String toString() {
        return String.format("[%s @ (%d,%d)] %s | %s | %s",
                bayId, row, col, connectorType, status,
                (dockedVehicle != null ? dockedVehicle.getLicensePlate() : "EMPTY"));
    }
}
