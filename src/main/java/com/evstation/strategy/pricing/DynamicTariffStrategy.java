package com.evstation.strategy.pricing;

import com.evstation.config.StationConfig;
import com.evstation.model.ChargingSession;
import java.time.LocalTime;

/**
 * Dynamic tariff strategy: applies peak surcharge between 17:00 and 21:00.
 */
public class DynamicTariffStrategy implements IBillingStrategy {

    @Override
    public double calculateRatePerKwh(ChargingSession session) {
        LocalTime time = (session != null) ? session.getStartTime().toLocalTime() : LocalTime.now();
        int hour = time.getHour();

        boolean isPeak = (hour >= StationConfig.PEAK_START_HOUR && hour < StationConfig.PEAK_END_HOUR);
        double rate = StationConfig.BASE_RATE_PER_KWH;

        if (isPeak) {
            rate *= StationConfig.PEAK_MULTIPLIER;
        }

        return rate;
    }
}
