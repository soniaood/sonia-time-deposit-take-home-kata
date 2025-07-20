package org.ikigaidigital.domain;

import org.ikigaidigital.entity.TimeDepositPlan;
import org.ikigaidigital.util.TimeDepositConstants;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component("student")
public class StudentTimeDepositInterestCalculator implements TimeDepositInterestCalculator {
    private static final BigDecimal STUDENT_INTEREST_RATE = TimeDepositPlan.STUDENT.getInterestRate();
    private static final int STUDENT_MAXIMUM_DAYS = 365;

    @Override
    public BigDecimal calculateInterest(@NonNull BigDecimal balance, @NonNull Integer days) {
        if (days > STUDENT_MAXIMUM_DAYS) {
            return BigDecimal.ZERO;
        }

        return balance.multiply(STUDENT_INTEREST_RATE)
                      .divide(BigDecimal.valueOf(TimeDepositConstants.MONTHS_IN_YEAR), 6, RoundingMode.HALF_UP);
    }
}
