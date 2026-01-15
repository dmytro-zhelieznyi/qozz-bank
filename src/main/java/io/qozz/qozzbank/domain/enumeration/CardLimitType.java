package io.qozz.qozzbank.domain.enumeration;

/**
 * CardLimitType defines supported spending and withdrawal limits
 * applied to a bank card.
 * Used in:
 * - card_limit_configs
 * - card_limit_counters
 */
public enum CardLimitType {
    SINGLE_TRANSACTION("Single transaction"),     // Maximum allowed amount for a single transaction.
    DAILY_AMOUNT("Daily amount"),                 // Maximum total amount allowed per day across all transactions.
    MONTHLY_AMOUNT("Monthly amount"),             // Maximum total amount allowed per month across all transactions.
    ATM_WITHDRAWAL_DAILY("ATM daily withdrawal"); // Maximum total amount allowed per day for ATM cash withdrawals.

    private final String value;

    CardLimitType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Resolves CardLimitType from a human-readable value.
     *
     * @param value human-readable limit description
     * @return matching CardLimitType
     */
    public static CardLimitType fromValue(String value) {
        for (CardLimitType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown CardLimitType value: " + value);
    }
}
