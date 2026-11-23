package com.evstation.service;

import com.evstation.enums.PaymentMode;
import com.evstation.model.Bill;
import com.evstation.model.ChargingSession;
import com.evstation.model.Driver;
import com.evstation.strategy.pricing.IBillingStrategy;
import java.util.UUID;

/**
 * Generates transparent, itemized bills for charging sessions.
 */
public class BillingService {

    private final IBillingStrategy pricingStrategy;

    public BillingService(IBillingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    public Bill generateBill(ChargingSession session, PaymentMode paymentMode) {
        if (session == null) {
            throw new IllegalArgumentException("Cannot generate bill for null session.");
        }

        double energy = session.getEnergyDeliveredKwh();
        double rate = pricingStrategy.calculateRatePerKwh(session);
        double subtotal = energy * rate;

        // Apply loyalty discount
        Driver driver = session.getDriver();
        double discountAmount = 0.0;
        if (driver != null) {
            discountAmount = subtotal * driver.getMembershipTier().getDiscountRate();
        }

        double discountedSubtotal = Math.max(0.0, subtotal - discountAmount);
        double tax = discountedSubtotal * 0.05; // 5% GST
        double total = discountedSubtotal + tax;

        String invId = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return new Bill(invId, session, rate, energy, subtotal, discountAmount, tax, total, paymentMode);
    }
}
