package org.ikigaidigital.mapper;

import org.ikigaidigital.dto.TimeDepositResponse;
import org.ikigaidigital.dto.WithdrawalResponse;
import org.ikigaidigital.entity.TimeDeposit;
import org.ikigaidigital.entity.TimeDepositPlan;
import org.ikigaidigital.util.TimeDepositConstants;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TimeDepositMapper {

    public TimeDepositResponse toTimeDepositResponse(TimeDeposit dbTimeDeposit) {

        return TimeDepositResponse.aBuilder()
                                  .withId(dbTimeDeposit.getId())
                                  .withPlanType(TimeDepositResponse.PlanType.valueOf(dbTimeDeposit.getPlanType()
                                                                                                  .name()))
                                  .withBalance(dbTimeDeposit.getBalance().setScale(TimeDepositConstants.FINANCIAL_SCALE,
                                                                                   TimeDepositConstants.FINANCIAL_ROUNDING_MODE))
                                  .withDays(dbTimeDeposit.getDays())
                                  .withWithdrawals(dbTimeDeposit.getWithdrawals()
                                                                .stream()
                                                                .map(withdrawal -> WithdrawalResponse.aBuilder()
                                                                                                     .withId(withdrawal.getId())
                                                                                                     .withAmount(
                                                                                                             withdrawal.getAmount()
                                                                                                                       .setScale(
                                                                                                                             TimeDepositConstants.FINANCIAL_SCALE,
                                                                                                                             TimeDepositConstants.FINANCIAL_ROUNDING_MODE))
                                                                                                     .withDate(
                                                                                                             withdrawal.getDate())
                                                                                                     .build())
                                                                .toList())
                                  .build();
    }

    public org.ikigaidigital.TimeDeposit toTimeDepositInternal(TimeDeposit timeDepositEntity) {
        return new org.ikigaidigital.TimeDeposit(timeDepositEntity.getId(),
                                                 timeDepositEntity.getPlanType().getRawPlanType(),
                                                 timeDepositEntity.getBalance().doubleValue(),
                                                 timeDepositEntity.getDays());
    }

    public void updateTimeDepositEntityFromInternal(TimeDeposit timeDepositEntity,
                                                    org.ikigaidigital.TimeDeposit internalTimeDeposit) {
        timeDepositEntity.setBalance(BigDecimal.valueOf(internalTimeDeposit.getBalance())
                                                  .setScale(TimeDepositConstants.FINANCIAL_SCALE,
                                                           TimeDepositConstants.FINANCIAL_ROUNDING_MODE));
    }
}