package org.ikigaidigital.domain;

import org.ikigaidigital.entity.TimeDepositPlan;
import org.ikigaidigital.util.TimeDepositConstants;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Component("basic")
public class BasicTimeDepositInterestCalculator implements TimeDepositInterestCalculator {
    private static final BigDecimal BASIC_INTEREST_RATE = TimeDepositPlan.BASIC.getInterestRate();

    @Override
    public BigDecimal calculateInterest(@NonNull BigDecimal balance, @NonNull Integer days) {
        return balance.multiply(BASIC_INTEREST_RATE)
                      .divide(BigDecimal.valueOf(TimeDepositConstants.MONTHS_IN_YEAR), 6, RoundingMode.HALF_UP);
    }
}
