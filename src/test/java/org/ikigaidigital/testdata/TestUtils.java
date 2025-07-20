package org.ikigaidigital.testdata;

import org.ikigaidigital.entity.TimeDeposit;
import org.ikigaidigital.entity.TimeDepositPlan;
import org.ikigaidigital.entity.Withdrawal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public final class TestUtils {

    private TestUtils() {
    }

    public static TimeDepositTestBuilder aTimeDepositBuilder() {
        return new TimeDepositTestBuilder();
    }

    public static TimeDepositInternalTestBuilder aTimeDepositInternalBuilder() {
        return new TimeDepositInternalTestBuilder();
    }

    public static WithdrawalTestBuilder aWithdrawalBuilder() {
        return new WithdrawalTestBuilder();
    }

    public static class TimeDepositTestBuilder {
        private Integer id = 1;
        private TimeDepositPlan planType = TimeDepositPlan.BASIC;
        private BigDecimal balance = new BigDecimal("1000.00");
        private Integer days = 30;
        private List<Withdrawal> withdrawals = Collections.emptyList();

        private TimeDepositTestBuilder() {
        }

        public TimeDepositTestBuilder withId(Integer id) {
            this.id = id;
            return this;
        }

        public TimeDepositTestBuilder withPlanType(TimeDepositPlan planType) {
            this.planType = planType;
            return this;
        }

        public TimeDepositTestBuilder withBalance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public TimeDepositTestBuilder withDays(Integer days) {
            this.days = days;
            return this;
        }

        public TimeDepositTestBuilder withWithdrawals(List<Withdrawal> withdrawals) {
            this.withdrawals = withdrawals;
            return this;
        }

        public TimeDeposit build() {
            TimeDeposit timeDeposit = new TimeDeposit();
            timeDeposit.setId(id);
            timeDeposit.setPlanType(planType);
            timeDeposit.setBalance(balance);
            timeDeposit.setDays(days);
            timeDeposit.setWithdrawals(withdrawals);
            return timeDeposit;
        }
    }

    public static class TimeDepositInternalTestBuilder {
        private int id = 1;
        private String planType = TimeDepositPlan.BASIC.getRawPlanType();
        private Double balance = 1000.00;
        private Integer days = 30;

        private TimeDepositInternalTestBuilder() {
        }

        public TimeDepositInternalTestBuilder withId(int id) {
            this.id = id;
            return this;
        }

        public TimeDepositInternalTestBuilder withPlanType(String planType) {
            this.planType = planType;
            return this;
        }

        public TimeDepositInternalTestBuilder withBalance(Double balance) {
            this.balance = balance;
            return this;
        }

        public TimeDepositInternalTestBuilder withDays(Integer days) {
            this.days = days;
            return this;
        }

        public org.ikigaidigital.TimeDeposit build() {
            return new org.ikigaidigital.TimeDeposit(id, planType, balance, days);
        }
    }

    public static class WithdrawalTestBuilder {
        private Integer id = 1;
        private TimeDeposit timeDeposit = new TimeDepositTestBuilder().build();
        private BigDecimal amount = new BigDecimal("100.00");
        private LocalDate date = LocalDate.now();

        private WithdrawalTestBuilder() {
        }

        public WithdrawalTestBuilder withId(Integer id) {
            this.id = id;
            return this;
        }

        public WithdrawalTestBuilder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public WithdrawalTestBuilder withTimeDeposit(TimeDeposit timeDeposit) {
            this.timeDeposit = timeDeposit;
            return this;
        }

        public WithdrawalTestBuilder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public Withdrawal build() {
            Withdrawal withdrawal = new Withdrawal();
            withdrawal.setId(id);
            withdrawal.setDate(date);
            withdrawal.setAmount(amount);
            withdrawal.setTimeDeposit(timeDeposit);
            return withdrawal;
        }
    }

}
