package org.ikigaidigital;

import org.ikigaidigital.domain.BasicTimeDepositInterestCalculator;
import org.ikigaidigital.domain.PremiumTimeDepositInterestCalculator;
import org.ikigaidigital.domain.StudentTimeDepositInterestCalculator;
import org.ikigaidigital.testdata.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class TimeDepositResponseCalculatorTest {

    private TimeDepositCalculator target;
    private BasicTimeDepositInterestCalculator basicTimeDepositInterestCalculator;
    private StudentTimeDepositInterestCalculator studentTimeDepositInterestCalculator;
    private PremiumTimeDepositInterestCalculator premiumTimeDepositInterestCalculator;

    @BeforeEach
    void setUp() {
         basicTimeDepositInterestCalculator =
                mock(BasicTimeDepositInterestCalculator.class);
         studentTimeDepositInterestCalculator =
                mock(StudentTimeDepositInterestCalculator.class);
         premiumTimeDepositInterestCalculator =
                mock(PremiumTimeDepositInterestCalculator.class);
        target = new TimeDepositCalculator(
                Map.of(
                        "basic", basicTimeDepositInterestCalculator,
                        "student", studentTimeDepositInterestCalculator,
                        "premium", premiumTimeDepositInterestCalculator
                )
        );
    }

    @Test
    @DisplayName("Should update balance with interest for a basic plan after 31 days")
    void updateBalance_basicPlan() {
        BigDecimal basicInterestRate = BigDecimal.valueOf(0.01);
        BigDecimal balance = BigDecimal.valueOf(1000.00);
        int days = 31;

        TimeDeposit basicTimeDeposit = TestUtils.aTimeDepositInternalBuilder()
                .withId(1)
                .withBalance(balance.doubleValue())
                .withPlanType("basic")
                .withDays(days).build();
        List<TimeDeposit> timeDeposits = Collections.singletonList(basicTimeDeposit);

        BigDecimal expectedInterest = balance.multiply(basicInterestRate)
                                                .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP)
                                                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedBalance = balance.add(expectedInterest).setScale(2, RoundingMode.HALF_UP);

        Mockito.when(basicTimeDepositInterestCalculator.calculateInterest(balance, basicTimeDeposit.getDays())).thenReturn(expectedInterest);

        target.updateBalance(timeDeposits);

        Mockito.verify(basicTimeDepositInterestCalculator, Mockito.times(1)).calculateInterest(balance, days);

        TimeDeposit updatedDeposit = timeDeposits.get(0);
        assertEquals(expectedBalance.doubleValue(), updatedDeposit.getBalance(),
                "The balance includes interest for the basic plan after 31 days.");
    }

    @Test
    @DisplayName("Should not apply interest for a basic plan within the first 30 days")
    void updateBalance_basicPlan_noInterestUnder31Days() {
        BigDecimal balance = BigDecimal.valueOf(2500.00);
        int days = 20;

        TimeDeposit basicTimeDeposit = TestUtils.aTimeDepositInternalBuilder()
                                                .withId(1)
                                                .withBalance(balance.doubleValue())
                                                .withPlanType("basic")
                                                .withDays(days).build();
        List<TimeDeposit> timeDeposits = Collections.singletonList(basicTimeDeposit);

        BigDecimal expectedBalance = balance.setScale(2, RoundingMode.HALF_UP);

        target.updateBalance(timeDeposits);

        Mockito.verifyNoInteractions(basicTimeDepositInterestCalculator);

        TimeDeposit updatedDeposit = timeDeposits.get(0);
        assertEquals(expectedBalance.doubleValue(), updatedDeposit.getBalance(), "The balance remains unchanged for the basic plan under 30 days.");
    }

    @Test
    @DisplayName("Should update balance with interest for a student plan after 31 days")
    void updateBalance_studentPlan() {
        BigDecimal studentInterestRate = BigDecimal.valueOf(0.03);
        BigDecimal balance = BigDecimal.valueOf(3000.00);
        int days = 31;

        TimeDeposit studentTimeDeposit = TestUtils.aTimeDepositInternalBuilder()
                                                .withId(1)
                                                .withBalance(balance.doubleValue())
                                                .withPlanType("student")
                                                .withDays(days).build();
        List<TimeDeposit> timeDeposits = Collections.singletonList(studentTimeDeposit);

        BigDecimal expectedInterest = balance.multiply(studentInterestRate)
                                             .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP)
                                             .setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedBalance = balance.add(expectedInterest).setScale(2, RoundingMode.HALF_UP);

        Mockito.when(studentTimeDepositInterestCalculator.calculateInterest(balance, days)).thenReturn(expectedInterest);

        target.updateBalance(timeDeposits);

        Mockito.verify(studentTimeDepositInterestCalculator, Mockito.times(1)).calculateInterest(balance, days);

        TimeDeposit updatedDeposit = timeDeposits.get(0);
        assertEquals(expectedBalance.doubleValue(), updatedDeposit.getBalance(),
                     "The balance includes interest for the student plan after 31 days.");
    }

    @Test
    @DisplayName("Should not apply interest for a student plan within the first 30 days")
    void updateBalance_studentPlan_noInterestUnder31Days() {
        BigDecimal balance = BigDecimal.valueOf(50.99);
        int days = 30;

        TimeDeposit studentTimeDeposit = TestUtils.aTimeDepositInternalBuilder()
                                                .withId(1)
                                                .withBalance(balance.doubleValue())
                                                .withPlanType("student")
                                                .withDays(days).build();
        List<TimeDeposit> timeDeposits = Collections.singletonList(studentTimeDeposit);

        BigDecimal expectedBalance = balance.setScale(2, RoundingMode.HALF_UP);

        target.updateBalance(timeDeposits);

        Mockito.verifyNoInteractions(studentTimeDepositInterestCalculator);

        TimeDeposit updatedDeposit = timeDeposits.get(0);
        assertEquals(expectedBalance.doubleValue(), updatedDeposit.getBalance(), "The balance remains unchanged for the basic plan under 30 days.");
    }

    @Test
    @DisplayName("Should not apply interest for a student plan exceeding 366 days")
    void updateBalanceStudentPlanAfter366Days() {
        BigDecimal balance = BigDecimal.valueOf(19821931.10);
        int days = 366;

        TimeDeposit studentTimeDeposit = TestUtils.aTimeDepositInternalBuilder()
                .withId(3)
                .withBalance(balance.doubleValue())
                .withPlanType("student")
                .withDays(days).build();
        List<TimeDeposit> timeDeposits = Collections.singletonList(studentTimeDeposit);

        BigDecimal expectedInterest = BigDecimal.ZERO;
        BigDecimal expectedBalance = balance.setScale(2, RoundingMode.HALF_UP);

        Mockito.when(studentTimeDepositInterestCalculator.calculateInterest(balance, days)).thenReturn(expectedInterest);

        target.updateBalance(timeDeposits);

        Mockito.verify(studentTimeDepositInterestCalculator, Mockito.times(1)).calculateInterest(balance, days);

        TimeDeposit updatedDeposit = timeDeposits.get(0);
        assertEquals(expectedBalance.doubleValue(), updatedDeposit.getBalance(), "The balance remains.");
    }

    @Test
    @DisplayName("Should update balance with interest for a premium plan after 45 days")
    void updateBalance_premiumPlan() {
        BigDecimal premiumInterestRate = BigDecimal.valueOf(0.05);
        BigDecimal balance = BigDecimal.valueOf(5000.00);
        int days = 60;

        TimeDeposit premiumTimeDeposit = TestUtils.aTimeDepositInternalBuilder()
                .withId(2)
                .withBalance(balance.doubleValue())
                .withPlanType("premium")
                .withDays(days).build();
        List<TimeDeposit> timeDeposits = Collections.singletonList(premiumTimeDeposit);

        BigDecimal expectedInterest = balance.multiply(premiumInterestRate)
                                             .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP)
                                             .setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedBalance = balance.add(expectedInterest).setScale(2, RoundingMode.HALF_UP);

        Mockito.when(premiumTimeDepositInterestCalculator.calculateInterest(balance, days)).thenReturn(expectedInterest);

        target.updateBalance(timeDeposits);

        Mockito.verify(premiumTimeDepositInterestCalculator, Mockito.times(1)).calculateInterest(balance, days);

        TimeDeposit updatedDeposit = timeDeposits.get(0);
        assertEquals(expectedBalance.doubleValue(), updatedDeposit.getBalance(),
                     "The balance includes interest for the premium plan after 45 days.");
    }

    @Test
    @DisplayName("Should not apply interest for a premium plan within the first 45 days")
    void updateBalance_premiumPlan_noInterestUnder45Days() {
        BigDecimal balance = BigDecimal.valueOf(1000.00);
        int days = 44;

        TimeDeposit premiumTimeDeposit = TestUtils.aTimeDepositInternalBuilder()
                .withId(2)
                .withBalance(balance.doubleValue())
                .withPlanType("premium")
                .withDays(days).build();
        List<TimeDeposit> timeDeposits = Collections.singletonList(premiumTimeDeposit);

        BigDecimal expectedInterest = BigDecimal.ZERO;
        BigDecimal expectedBalance = balance.setScale(2, RoundingMode.HALF_UP);
        Mockito.when(premiumTimeDepositInterestCalculator.calculateInterest(balance, days)).thenReturn(expectedInterest);

        target.updateBalance(timeDeposits);

        Mockito.verify(premiumTimeDepositInterestCalculator, Mockito.times(1)).calculateInterest(balance, days);

        TimeDeposit updatedDeposit = timeDeposits.get(0);
        assertEquals(expectedBalance.doubleValue(), updatedDeposit.getBalance(), "The balance remains unchanged for the premium plan under 45 days.");
    }

    @Test
    @DisplayName("Should handle empty list of time deposits")
    void updateBalance_emptyList() {
        List<TimeDeposit> timeDeposits = Collections.emptyList();

        target.updateBalance(timeDeposits);

        assertThat(timeDeposits).isEmpty();
    }

    @Test
    @DisplayName("Should handle multiple time deposits with different plans at the same time")
    void updateBalance_multiplePlans() {
        TimeDeposit basicTimeDeposit = new TimeDeposit(1, "basic", 10000.00, 31);
        TimeDeposit studentTimeDeposit = new TimeDeposit(3, "student", 15000.00, 61);
        TimeDeposit premiumTimeDeposit = new TimeDeposit(2, "premium", 20000.00, 136);

        List<TimeDeposit> timeDeposits = Arrays.asList(basicTimeDeposit, studentTimeDeposit, premiumTimeDeposit);

        double basicExpectedBalance = 10008.33; // 10000 + (10000 * 0.01 / 12)
        double studentExpectedBalance = 15037.50; // 15000 + (15000 * 0.03 / 12)
        double premiumExpectedBalance = 20083.33; // 20000 + (20000 * 0.05 / 12)

        Mockito.when(basicTimeDepositInterestCalculator.calculateInterest(BigDecimal.valueOf(basicTimeDeposit.getBalance()), basicTimeDeposit.getDays()))
                .thenReturn(BigDecimal.valueOf((basicTimeDeposit.getBalance() * 0.01 / 12)).setScale(2, RoundingMode.HALF_UP));
        Mockito.when(studentTimeDepositInterestCalculator.calculateInterest(BigDecimal.valueOf(studentTimeDeposit.getBalance()), studentTimeDeposit.getDays()))
                .thenReturn(BigDecimal.valueOf((studentTimeDeposit.getBalance() * 0.03 / 12)).setScale(2, RoundingMode.HALF_UP));
        Mockito.when(premiumTimeDepositInterestCalculator.calculateInterest(BigDecimal.valueOf(premiumTimeDeposit.getBalance()), premiumTimeDeposit.getDays()))
                .thenReturn(BigDecimal.valueOf((premiumTimeDeposit.getBalance() * 0.05 / 12)).setScale(2, RoundingMode.HALF_UP));

        target.updateBalance(timeDeposits);

        Mockito.verify(basicTimeDepositInterestCalculator, Mockito.times(1)).calculateInterest(BigDecimal.valueOf(10000.00), basicTimeDeposit.getDays());
        Mockito.verify(studentTimeDepositInterestCalculator, Mockito.times(1)).calculateInterest(BigDecimal.valueOf(15000.00), studentTimeDeposit.getDays());
        Mockito.verify(premiumTimeDepositInterestCalculator, Mockito.times(1)).calculateInterest(BigDecimal.valueOf(20000.00), premiumTimeDeposit.getDays());

        assertEquals(basicExpectedBalance, basicTimeDeposit.getBalance(), "Basic plan balance updated correctly.");
        assertEquals(studentExpectedBalance, studentTimeDeposit.getBalance(), "Student plan balance updated correctly.");
        assertEquals(premiumExpectedBalance, premiumTimeDeposit.getBalance(), "Premium plan balance updated correctly.");
    }
}
