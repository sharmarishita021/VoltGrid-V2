package com.evstation.validation;

import com.evstation.model.Bill;
import com.evstation.model.ChargingSession;
import java.time.format.DateTimeFormatter;

/**
 * Formats tax receipts using StringBuilder.
 */
public class ReceiptFormatter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String formatReceipt(Bill bill) {
        if (bill == null) return "No bill record.";

        ChargingSession s = bill.getSession();
        StringBuilder sb = new StringBuilder(512);

        sb.append("=====================================================\n");
        sb.append("             VOLTGRID EV CHARGING STATION            \n");
        sb.append("                 TAX INVOICE / RECEIPT               \n");
        sb.append("=====================================================\n");
        sb.append(String.format("Invoice No    : %s\n", bill.getInvoiceNumber()));
        sb.append(String.format("Date & Time   : %s\n", bill.getBilledAt().format(DATE_FORMATTER)));
        sb.append(String.format("Session ID    : %s\n", s.getSessionId()));
        sb.append("-----------------------------------------------------\n");
        sb.append(String.format("Vehicle Plate : %s (%s)\n", s.getVehicle().getLicensePlate(), s.getVehicle().getModel()));
        sb.append(String.format("Customer      : %s (%s)\n", s.getDriver().getName(), s.getDriver().getMembershipTier()));
        sb.append(String.format("Bay ID        : %s\n", s.getBay().getBayId()));
        sb.append("-----------------------------------------------------\n");
        sb.append(String.format("Initial SoC   : %6.1f %%\n", s.getInitialSoc()));
        sb.append(String.format("Final SoC     : %6.1f %%\n", s.getFinalSoc()));
        sb.append(String.format("Energy Disp   : %6.2f kWh\n", bill.getEnergyKwh()));
        sb.append(String.format("Tariff Rate   : $ %5.2f / kWh\n", bill.getBaseRate()));
        sb.append(String.format("Subtotal      : $ %5.2f\n", bill.getSubtotal()));
        sb.append(String.format("Discount      : -$%5.2f\n", bill.getDiscountAmount()));
        sb.append(String.format("Tax (5%% GST)  : $ %5.2f\n", bill.getTaxAmount()));
        sb.append("=====================================================\n");
        sb.append(String.format("TOTAL PAID    : $ %5.2f (%s)\n", bill.getTotalPayable(), bill.getPaymentMode()));
        sb.append("=====================================================\n");

        return sb.toString();
    }
}
