package org.ikigaidigital.dto;

import java.math.BigDecimal;
import java.util.List;

public record TimeDepositResponse(
    Integer id,
    PlanType planType,
    BigDecimal balance,
    Integer days,
    List<WithdrawalResponse> withdrawals
) {

    public enum PlanType {
        BASIC,
        STUDENT,
        PREMIUM
    }

    public static TimeDepositResponseBuilder aBuilder() {
        return new TimeDepositResponseBuilder();
    }

    public static final class TimeDepositResponseBuilder {
        private Integer id;
        private PlanType planType;
        private BigDecimal balance;
        private Integer days;
        private List<WithdrawalResponse> withdrawals;

        private TimeDepositResponseBuilder() {
        }

        public TimeDepositResponseBuilder withId(Integer id) {
            this.id = id;
            return this;
        }

        public TimeDepositResponseBuilder withPlanType(PlanType planType) {
            this.planType = planType;
            return this;
        }

        public TimeDepositResponseBuilder withBalance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public TimeDepositResponseBuilder withDays(Integer days) {
            this.days = days;
            return this;
        }

        public TimeDepositResponseBuilder withWithdrawals(List<WithdrawalResponse> withdrawals) {
            this.withdrawals = withdrawals;
            return this;
        }

        public TimeDepositResponse build() {
            return new TimeDepositResponse(id, planType, balance, days, withdrawals);
        }
    }
}
