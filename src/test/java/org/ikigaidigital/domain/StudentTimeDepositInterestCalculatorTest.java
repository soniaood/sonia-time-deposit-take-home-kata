package org.ikigaidigital.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StudentTimeDepositInterestCalculatorTest {

    private StudentTimeDepositInterestCalculator target;

    @BeforeEach
    void setUp() {
        target = new StudentTimeDepositInterestCalculator();
    }

    @Test
    @DisplayName("Should return ZERO interest for days exceeding one year limit")
    void calculateInterest_exceedingMaxDays_returnsZero() {
        BigDecimal balance = BigDecimal.valueOf(1000.00);
        int days = 366;
        BigDecimal result = target.calculateInterest(balance, days);

        assertEquals(BigDecimal.ZERO, result, "Interest should be ZERO for days exceeding one year limit on student plans");
    }

    @Test
    @DisplayName("Should calculate interest correctly")
    void calculateInterest_returnsCorrectInterest() {
        BigDecimal balance = BigDecimal.valueOf(1000.00);
        int days = 31;
        BigDecimal interestRate = BigDecimal.valueOf(0.03);

        BigDecimal expectedInterest = balance.multiply(interestRate)
                                              .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);

        BigDecimal result = target.calculateInterest(balance, days);

        assertEquals(expectedInterest, result, "Interest should be calculated correctly");
        assertEquals(6, result.scale(), "The interest should be scaled to 6 decimal places.");
    }

    @Test
    @DisplayName("Should calculate interest correctly on high balance")
    void calculateInterest_highBalance_returnsCorrectInterest() {
        BigDecimal balance = BigDecimal.valueOf(1234567.99);
        int days = 60;
        BigDecimal interestRate = BigDecimal.valueOf(0.03);

        BigDecimal expectedInterest = balance.multiply(interestRate)
                                             .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);

        BigDecimal result = target.calculateInterest(balance, days);

        assertEquals(expectedInterest, result, "Interest should be calculated correctly when the balance is high");
        assertEquals(6, result.scale(), "The interest should be scaled to 6 decimal places.");
    }
}