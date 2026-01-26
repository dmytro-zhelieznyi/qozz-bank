package io.qozz.qozzbank.domain.enumeration;

import lombok.Getter;

/**
 * AccountType defines the category of the bank account.
 * This determines the rules for interest, withdrawals, and limits.
 */
@Getter
public enum AccountType {
    CHECKING("Checking"),     // Regular transaction account for daily use.
    SAVINGS("Savings"),       // Interest-bearing account for wealth accumulation.
    DEPOSIT("Deposit"),       // Term deposit account with restricted access.
    CREDIT("Credit"),         // Line of credit or credit card account.
    INVESTMENT("Investment"), // Brokerage or wealth management account.
    LOAN("Loan");             // Liability account representing a debt.

    private final String value;

    AccountType(String value) {
        this.value = value;
    }

    /**
     * Returns the enum constant matching the given human-readable value.
     *
     * @param value human-readable name of the account type
     * @return AccountType enum
     * @throws IllegalArgumentException if no match found
     */
    public static AccountType fromValue(String value) {
        for (AccountType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown AccountType value: " + value);
    }
}