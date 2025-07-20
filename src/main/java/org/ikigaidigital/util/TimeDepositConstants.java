package org.ikigaidigital.util;

import java.math.RoundingMode;

public final class TimeDepositConstants {
    private TimeDepositConstants() {

    }

    public static final int MONTHS_IN_YEAR = 12;
    public static final int INTEREST_MINIMUM_DAYS = 31;
    public static final int FINANCIAL_SCALE = 2;
    public static final RoundingMode FINANCIAL_ROUNDING_MODE = RoundingMode.HALF_UP;

}
