package com.evstation.strategy.pricing;

import com.evstation.model.ChargingSession;

/**
 * Strategy interface for tariff calculation.
 */
public interface IBillingStrategy {

    double calculateRatePerKwh(ChargingSession session);
}
