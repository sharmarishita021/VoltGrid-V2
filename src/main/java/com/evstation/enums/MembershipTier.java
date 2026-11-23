package com.evstation.enums;

/**
 * Customer membership tier and discount rate.
 */
public enum MembershipTier {
    STANDARD(0.00, 1.0),
    GOLD(0.10, 1.2),
    VIP(0.20, 1.5);

    private final double discountRate;
    private final double priorityMultiplier;

    MembershipTier(double discountRate, double priorityMultiplier) {
        this.discountRate = discountRate;
        this.priorityMultiplier = priorityMultiplier;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public double getPriorityMultiplier() {
        return priorityMultiplier;
    }
}
