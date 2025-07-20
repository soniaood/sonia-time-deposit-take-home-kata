package org.ikigaidigital.domain;

import org.ikigaidigital.entity.TimeDepositPlan;
import org.ikigaidigital.util.TimeDepositConstants;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component("premium")
public class PremiumTimeDepositInterestCalculator implements TimeDepositInterestCalculator {
    private static final BigDecimal PREMIUM_INTEREST_RATE = TimeDepositPlan.PREMIUM.getInterestRate();
    private static final int PREMIUM_MINIMUM_DAYS = 46;

    @Override
    public BigDecimal calculateInterest(@NonNull BigDecimal balance, @NonNull Integer days) {
        if (days < PREMIUM_MINIMUM_DAYS) {
            return BigDecimal.ZERO;
        }

        return balance.multiply(PREMIUM_INTEREST_RATE)
                      .divide(BigDecimal.valueOf(TimeDepositConstants.MONTHS_IN_YEAR), 6, RoundingMode.HALF_UP);
    }
}
