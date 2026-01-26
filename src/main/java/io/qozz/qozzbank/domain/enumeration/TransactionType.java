package io.qozz.qozzbank.domain.enumeration;

import lombok.Getter;

/**
 * TransactionType represents the type of financial transaction.
 * It helps the system understand the nature of each movement of funds.
 */
@Getter
public enum TransactionType {
    TRANSFER("Transfer"),         // Internal money transfer between accounts.
    DEPOSIT("Deposit"),           // Money deposited to the account.
    WITHDRAWAL("Withdrawal"),     // Money withdrawn from the account.
    CARD_PAYMENT("Card Payment"), // Payment using a card.
    FEE("Fee");                   // Commission or service fee.

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    /**
     * Returns the enum constant matching the given human-readable value.
     *
     * @param value human-readable name of the transaction type
     * @return TransactionType enum constant
     * @throws IllegalArgumentException if no match found
     */
    public static TransactionType fromValue(String value) {
        for (TransactionType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown TransactionType value: " + value);
    }
}
