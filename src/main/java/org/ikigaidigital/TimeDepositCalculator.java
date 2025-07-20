package org.ikigaidigital;

import org.ikigaidigital.domain.TimeDepositInterestCalculator;
import org.ikigaidigital.util.TimeDepositConstants;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class TimeDepositCalculator {

    private final Map<String, TimeDepositInterestCalculator> interestCalculators;

    public TimeDepositCalculator(Map<String, TimeDepositInterestCalculator> interestCalculators) {
        this.interestCalculators = interestCalculators;
    }

    public void updateBalance(List<TimeDeposit> xs) {
        for (TimeDeposit deposit : xs) {
            if (deposit.getBalance() == null || deposit.getDays() < TimeDepositConstants.INTEREST_MINIMUM_DAYS) {
                deposit.setBalance(deposit.getBalance());
                continue;
            }

            TimeDepositInterestCalculator calculator = interestCalculators.get(deposit.getPlanType());
            BigDecimal interest = calculator.calculateInterest(BigDecimal.valueOf(deposit.getBalance()), deposit.getDays());

            deposit.setBalance(
                BigDecimal.valueOf(deposit.getBalance())
                    .add(interest)
                    .setScale(TimeDepositConstants.FINANCIAL_SCALE, TimeDepositConstants.FINANCIAL_ROUNDING_MODE)
                    .doubleValue()
            );
        }
    }
}
