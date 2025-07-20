package org.ikigaidigital.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Optional;

@Converter(autoApply = true)
public class TimeDepositPlanConverter implements AttributeConverter<TimeDepositPlan, String> {

    @Override
    public String convertToDatabaseColumn(TimeDepositPlan plan) {
        return Optional.ofNullable(plan)
                       .map(TimeDepositPlan::getRawPlanType)
                       .orElse(null);
    }

    @Override
    public TimeDepositPlan convertToEntityAttribute(String rawPlanType) {
        return Optional.ofNullable(rawPlanType)
                       .map(TimeDepositPlan::fromRawPlanType)
                       .orElseThrow(() -> new IllegalArgumentException("Unknown or null TimeDepositPlan value in DB: " + rawPlanType));
    }
}