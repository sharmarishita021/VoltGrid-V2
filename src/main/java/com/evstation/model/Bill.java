package com.evstation.model;

import com.evstation.enums.PaymentMode;
import java.time.LocalDateTime;

/**
 * Itemized billing invoice.
 */
public class Bill {

    private final String invoiceNumber;
    private final ChargingSession session;
    private final double baseRate;
    private final double energyKwh;
    private final double subtotal;
    private final double discountAmount;
    private final double taxAmount;
    private final double totalPayable;
    private final PaymentMode paymentMode;
    private final LocalDateTime billedAt;

    public Bill(String invoiceNumber, ChargingSession session, double baseRate,
                double energyKwh, double subtotal, double discountAmount,
                double taxAmount, double totalPayable, PaymentMode paymentMode) {
        this.invoiceNumber = invoiceNumber;
        this.session = session;
        this.baseRate = baseRate;
        this.energyKwh = energyKwh;
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.taxAmount = taxAmount;
        this.totalPayable = totalPayable;
        this.paymentMode = paymentMode;
        this.billedAt = LocalDateTime.now();
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public ChargingSession getSession() {
        return session;
    }

    public double getBaseRate() {
        return baseRate;
    }

    public double getEnergyKwh() {
        return energyKwh;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public double getTotalPayable() {
        return totalPayable;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public LocalDateTime getBilledAt() {
        return billedAt;
    }

    @Override
    public String toString() {
        return String.format("Bill[%s] Total: $%.2f (%.2f kWh) | %s", invoiceNumber, totalPayable, energyKwh, paymentMode);
    }
}
