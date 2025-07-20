package org.ikigaidigital.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WithdrawalResponse(
        Integer id,
        BigDecimal amount,
        LocalDate date
) {

    public static WithdrawalResponseBuilder aBuilder() {
        return new WithdrawalResponseBuilder();
    }

    public static final class WithdrawalResponseBuilder {
        private Integer id;
        private BigDecimal amount;
        private LocalDate date;

        private WithdrawalResponseBuilder() {
        }

        public WithdrawalResponseBuilder withId(Integer id) {
            this.id = id;
            return this;
        }

        public WithdrawalResponseBuilder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public WithdrawalResponseBuilder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public WithdrawalResponse build() {
            return new WithdrawalResponse(id, amount, date);
        }
    }
}
