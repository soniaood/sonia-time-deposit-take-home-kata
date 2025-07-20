package org.ikigaidigital.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PremiumTimeDepositInterestCalculatorTest {

    private PremiumTimeDepositInterestCalculator target;

    @BeforeEach
    void setUp() {
        target = new PremiumTimeDepositInterestCalculator();
    }

    @Test
    @DisplayName("Should return ZERO interest if days is 45 or less")
    void calculateInterest_days45OrLess_zeroInterest() {
        BigDecimal balance = BigDecimal.valueOf(1000.00);
        int days = 45;

        BigDecimal result = target.calculateInterest(balance, days);

        assertEquals(BigDecimal.ZERO, result, "Interest should be ZERO for 45 days or less");
    }

    @Test
    @DisplayName("Should calculate interest correctly for more than 45 days")
    void calculateInterest_moreThan45Days_returnsCorrectInterest() {
        BigDecimal balance = BigDecimal.valueOf(1000.00);
        int days = 60;
        BigDecimal interestRate = BigDecimal.valueOf(0.05);

        BigDecimal expectedInterest = balance.multiply(interestRate)
                                              .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);

        BigDecimal result = target.calculateInterest(balance, days);

        assertEquals(expectedInterest, result, "Interest should be calculated correctly for more than 45 days");
        assertEquals(6, result.scale(), "The interest should be scaled to 6 decimal places.");
    }

    @Test
    @DisplayName("Should calculate interest correctly for more than 45 days on high balance")
    void calculateInterest_moreThan45Days_highBalance_returnsCorrectInterest() {
        BigDecimal balance = BigDecimal.valueOf(1234567.99);
        int days = 366;
        BigDecimal interestRate = BigDecimal.valueOf(0.05);

        BigDecimal expectedInterest = balance.multiply(interestRate)
                                             .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);

        BigDecimal result = target.calculateInterest(balance, days);

        assertEquals(expectedInterest, result, "Interest should be calculated correctly for more than 45 days when the balance is high");
        assertEquals(6, result.scale(), "The interest should be scaled to 6 decimal places.");
    }
}