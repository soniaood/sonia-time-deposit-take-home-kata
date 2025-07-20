package org.ikigaidigital.mapper;

import org.ikigaidigital.dto.TimeDepositResponse;
import org.ikigaidigital.entity.TimeDeposit;
import org.ikigaidigital.entity.TimeDepositPlan;
import org.ikigaidigital.testdata.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;

import static org.ikigaidigital.testdata.TestUtils.aTimeDepositBuilder;
import static org.ikigaidigital.testdata.TestUtils.aTimeDepositInternalBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TimeDepositMapperTest {

    private final TimeDepositMapper target = new TimeDepositMapper();

    @Test
    @DisplayName("Should map TimeDeposit entity to TimeDepositResponse correctly")
    void mapToTimeDepositResponse() {
        TimeDeposit timeDepositEntity = aTimeDepositBuilder().withId(1)
                                                             .withPlanType(TimeDepositPlan.BASIC)
                                                             .withBalance(new BigDecimal("1000.00"))
                                                             .withDays(30)
                                                             .withWithdrawals(Collections.singletonList(TestUtils.aWithdrawalBuilder()
                                                                    .withId(1)
                                                                    .withAmount(new BigDecimal("100.00"))
                                                                    .withDate(LocalDate.now()).build()))
                                                             .build();

        var timeDepositResponse = target.toTimeDepositResponse(timeDepositEntity);

        assertNotNull(timeDepositResponse, "Time deposit response should not be null.");
        assertEquals(1, timeDepositResponse.id(), "Time deposit id should match.");
        assertEquals(TimeDepositResponse.PlanType.BASIC, timeDepositResponse.planType(), "Plan type should match.");
        assertEquals(new BigDecimal("1000.00").setScale(2, RoundingMode.HALF_UP), timeDepositResponse.balance());
        assertEquals(30, timeDepositResponse.days(), "Days should match.");

        assertNotNull(timeDepositResponse.withdrawals(), "Withdrawals list should not be null.");
        assertEquals(1, timeDepositResponse.withdrawals().size(), "Withdrawals size should match.");
        assertEquals(1, timeDepositResponse.withdrawals().get(0).id(), "Withdrawal id should match.");
        assertEquals(new BigDecimal("100.00").setScale(2, RoundingMode.HALF_UP), timeDepositResponse.withdrawals().get(0).amount(), "Withdrawal amount should match.");
        assertEquals(LocalDate.now(), timeDepositResponse.withdrawals().get(0).date(), "Withdrawal date should match.");
    }

    @Test
    @DisplayName("Should convert TimeDeposit entity to internal representation correctly")
    void toTimeDepositInternal() {
        TimeDeposit timeDepositEntity = aTimeDepositBuilder().withId(2)
                                                             .withPlanType(TimeDepositPlan.STUDENT)
                                                             .withBalance(new BigDecimal("10000.00"))
                                                             .withDays(300)
                                                             .build();

        var internalTimeDeposit = target.toTimeDepositInternal(timeDepositEntity);

        assertNotNull(internalTimeDeposit, "Internal time deposit should not be null.");
        assertEquals(2, internalTimeDeposit.getId(), "ID should match.");
        assertEquals("student", internalTimeDeposit.getPlanType(), "Plan type should match.");
        assertEquals(10000.00, internalTimeDeposit.getBalance(), "Balance should match.");
    }

    @Test
    @DisplayName("Should update TimeDeposit entity from internal representation correctly")
    void updateTimeDepositEntityFromInternal() {
        TimeDeposit timeDepositEntity = aTimeDepositBuilder().withId(3)
                                                             .withPlanType(TimeDepositPlan.PREMIUM)
                                                             .withBalance(new BigDecimal("5000.00"))
                                                             .withDays(60)
                                                             .build();

        var internalTimeDeposit = aTimeDepositInternalBuilder().withId(3)
                                                               .withPlanType("premium")
                                                               .withBalance(5200.00)
                                                               .withDays(60)
                                                               .build();

        target.updateTimeDepositEntityFromInternal(timeDepositEntity, internalTimeDeposit);

        assertNotNull(timeDepositEntity, "Time deposit entity should not be null.");
        assertEquals(new BigDecimal("5200.00").setScale(2, RoundingMode.HALF_UP), timeDepositEntity.getBalance(), "Balance should be updated.");
    }
}