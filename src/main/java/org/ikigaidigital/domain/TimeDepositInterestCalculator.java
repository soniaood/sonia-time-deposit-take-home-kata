package org.ikigaidigital.domain;

import org.springframework.lang.NonNull;

import java.math.BigDecimal;

public interface TimeDepositInterestCalculator {
    BigDecimal calculateInterest(@NonNull BigDecimal balance,
                                 @NonNull Integer days);
}
