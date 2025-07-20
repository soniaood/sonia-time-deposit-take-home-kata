package org.ikigaidigital.entity;

import java.math.BigDecimal;

public enum TimeDepositPlan {
    BASIC("basic", BigDecimal.valueOf(0.01)),
    STUDENT("student", BigDecimal.valueOf(0.03)),
    PREMIUM("premium", BigDecimal.valueOf(0.05));

    private final String rawPlanType;
    private final BigDecimal interestRate;

    TimeDepositPlan(String rawPlanType, BigDecimal interestRate) {
        this.rawPlanType = rawPlanType;
        this.interestRate = interestRate;
    }

    public static TimeDepositPlan fromRawPlanType(String rawPlanType) {
        for (TimeDepositPlan plan : TimeDepositPlan.values()) {
            if (plan.getRawPlanType().equalsIgnoreCase(rawPlanType)) {
                return plan;
            }
        }
        throw new IllegalArgumentException("Unknown TimeDepositPlan: " + rawPlanType);
    }

    public String getRawPlanType() {
        return rawPlanType;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }
}
