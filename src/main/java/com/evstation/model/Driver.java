package com.evstation.model;

import com.evstation.enums.MembershipTier;
import java.util.Objects;

/**
 * Represents a registered customer/driver.
 */
public class Driver {

    private final String driverId;
    private final String name;
    private final String phone;
    private MembershipTier membershipTier;
    private double walletBalance;

    public Driver(String driverId, String name, String phone, MembershipTier membershipTier, double walletBalance) {
        this.driverId = driverId;
        this.name = name;
        this.phone = phone;
        this.membershipTier = membershipTier;
        this.walletBalance = Math.max(0.0, walletBalance);
    }

    public void creditWallet(double amount) {
        if (amount > 0) {
            this.walletBalance += amount;
        }
    }

    public boolean debitWallet(double amount) {
        if (amount > 0 && this.walletBalance >= amount) {
            this.walletBalance -= amount;
            return true;
        }
        return false;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public MembershipTier getMembershipTier() {
        return membershipTier;
    }

    public void setMembershipTier(MembershipTier membershipTier) {
        this.membershipTier = membershipTier;
    }

    public double getWalletBalance() {
        return walletBalance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Driver driver)) return false;
        return Objects.equals(driverId, driver.driverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) | %s | Wallet: $%.2f", name, driverId, membershipTier, walletBalance);
    }
}
